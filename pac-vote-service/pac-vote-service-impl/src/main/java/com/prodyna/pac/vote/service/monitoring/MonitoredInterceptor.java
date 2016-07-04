package com.prodyna.pac.vote.service.monitoring;

import com.prodyna.pac.vote.annotations.Monitored;

import java.lang.reflect.Method;

import javax.ejb.Asynchronous;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Interceptor that does monitoring of each method invocation with all parameters and some performance information.
 */
@Monitored
@Interceptor
public class MonitoredInterceptor {
    private static final String WELD = "$Proxy$";
    public static boolean logEnabled = true;

    @Inject
    Event<MonitoredEvent> event;

    @AroundInvoke
    public Object transformReturn(InvocationContext context) throws Exception {

        String className = context.getTarget().getClass().getName();
        if (className.contains(WELD)) {
            final int i = className.indexOf(WELD);
            className = className.substring(0, i);
        }
        final Logger log = LoggerFactory.getLogger(className);
        final Method m = context.getMethod();
        final String signature = m.getName() + this.params(context);
        if (logEnabled) {
            log.info(">>> " + signature);
        }
        final long before = System.currentTimeMillis();
        Object ret;
        try {
            ret = context.proceed();
        } catch (final Exception e) {
            if (logEnabled) {
                log.info("<<< " + signature + " ! " + e.getClass().getName());
            }
            throw e;
        } finally {
            final long after = System.currentTimeMillis();
            final long diff = after - before;

            if (logEnabled) {
                log.info("<<< " + signature + " {" + diff + "} ");
            }

            this.addMonitoredEntry(new MonitoredEvent(className, signature, diff));
        }
        return ret;
    }

    @Asynchronous
    protected void addMonitoredEntry(MonitoredEvent monitoredEvent) {
        this.event.fire(monitoredEvent);
    }

    private String params(InvocationContext context) {
        final StringBuffer sb = new StringBuffer();
        for (final Object p : context.getParameters()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(p == null ? "null" : p.getClass().getName());
        }
        String out = sb.toString();
        if (out.length() > 200) {
            out = sb.substring(0, 199) + "...";
        }
        return "(" + out + ")";
    }
}