package com.prodyna.pac.vote.service.monitoring;

/**
 *
 * @author cschaefer
 *
 */
public class MonitoredEvent {

    private final String className;
    private final String signature;
    private final long duration;

    public MonitoredEvent(String className, String signature, long duration) {
        this.className = className;
        this.signature = signature;
        this.duration = duration;
    }

    public String getClassName() {
        return this.className;
    }

    public String getSignature() {
        return this.signature;
    }

    public long getDuration() {
        return this.duration;
    }

}
