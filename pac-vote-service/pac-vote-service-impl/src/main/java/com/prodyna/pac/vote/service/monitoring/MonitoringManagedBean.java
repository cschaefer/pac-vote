package com.prodyna.pac.vote.service.monitoring;

import com.prodyna.pac.vote.managed.MonitoringMXBean;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.management.MBeanServer;
import javax.management.ObjectName;

@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Singleton
@Startup
@LocalBean
public class MonitoringManagedBean implements MonitoringMXBean {

    private final Map<String, MonitoredEntry> map = new HashMap<>();


    /**
     * Constant for 100 percent
     */
    private static final double PERCENT_100 = 100.0;

    private MBeanServer platformMBeanServer;
    private ObjectName objectName = null;

    private boolean enabled = false;

    @Override
    @PostConstruct
    public void registerInJMX() {
        try {
            this.objectName = new ObjectName("company:service=" + this.getClass().getName());
            this.platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            this.platformMBeanServer.registerMBean(this, this.objectName);
        } catch (final Exception e) {
            throw new IllegalStateException("Problem during registration of Monitoring into JMX: " + e);
        }


    }

    @Override
    @PreDestroy
    public void unregisterFromJMX() {
        try {
            this.platformMBeanServer.unregisterMBean(this.objectName);
        } catch (final Exception e) {
            throw new IllegalStateException("Problem during unregistration of Monitoring into JMX: " + e);
        }
    }

    @Lock(LockType.READ)
    @Override
    public String print() {
        final Collection<CSVRow> rows = new ArrayList<>();
        final long allTime = this.calculateAllTime();
        for (final Map.Entry<String, MonitoredEntry> entry : this.map.entrySet()) {
            final CSVRow row = this.buildRow(entry);
            rows.add(row);
        }
        return new CSVBuilder<CSVRow>()
                .addColumn("class", CSVRow::getClassName)
                .addColumn("signature", CSVRow::getSignature)
                .addColumn("all", (row) -> Long.toString(row.getAll()))
                .addColumn("min", (row) -> Long.toString(row.getMin()))
                .addColumn("max", (row) -> Long.toString(row.getMax()))
                .addColumn("count", (row) -> Long.toString(row.getCount()))
                .addColumn("average", (row) -> Long.toString(row.getAll() / row.getCount()))
                .addColumn("%", (row) -> Double.toString((PERCENT_100 / allTime) * row.getAll())).build(rows);
    }

    @Lock(LockType.WRITE)
    @Override
    public void clear() {
        this.map.clear();
    }

    @Lock(LockType.WRITE)
    @Override
    public void enabled() {
        this.enabled = true;
    }

    @Lock(LockType.WRITE)
    @Override
    public void disable() {
        this.enabled = false;
        this.clear();
    }


    @Lock(LockType.WRITE)
    public void listenToEvent(@Observes MonitoredEvent monitoredEvent) {

        if (!this.enabled) {
            return;
        }

        final String key = monitoredEvent.getClassName() + monitoredEvent.getSignature();
        MonitoredEntry monitorEntry = this.map.get(key);
        if (monitorEntry == null) {
            monitorEntry = new MonitoredEntry(monitoredEvent.getClassName(), monitoredEvent.getSignature());
            this.map.put(key, monitorEntry);
        }
        monitorEntry.addTime(monitoredEvent.getDuration());

    }

    private long calculateAllTime() {
        long allTime = 0;
        for (final Map.Entry<String, MonitoredEntry> entry : this.map.entrySet()) {
            allTime += entry.getValue().getAll();
        }
        return allTime;
    }

    private CSVRow buildRow(Map.Entry<String, MonitoredEntry> entry) {
        final CSVRow row = new CSVRow();
        final MonitoredEntry value = entry.getValue();
        row.setClassName(value.getClassName());
        row.setSignature(value.getSignature());
        row.setMax(value.getMax());
        row.setMin(value.getMin());
        row.setAll(value.getAll());
        row.setCount(value.getCount());
        return row;
    }

    /**
     * Model for CSV generation.
     */
    private static class CSVRow {

        private String className;
        private String signature;
        private long max;
        private long min;
        private long all;
        private long count;

        String getClassName() {
            return this.className;
        }

        void setClassName(String className) {
            this.className = className;
        }

        String getSignature() {
            return this.signature;
        }

        void setSignature(String signature) {
            this.signature = signature;
        }

        void setMax(long max) {
            this.max = max;
        }

        long getMax() {
            return this.max;
        }

        void setMin(long min) {
            this.min = min;
        }

        long getMin() {
            return this.min;
        }

        void setAll(long all) {
            this.all = all;
        }

        long getAll() {
            return this.all;
        }

        void setCount(long count) {
            this.count = count;
        }

        long getCount() {
            return this.count;
        }
    }


}
