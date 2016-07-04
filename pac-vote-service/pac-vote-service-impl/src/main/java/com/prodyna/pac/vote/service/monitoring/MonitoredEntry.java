package com.prodyna.pac.vote.service.monitoring;

public class MonitoredEntry {

    private final String className;
    private final String signature;

    private long all;
    private long max = Long.MIN_VALUE;
    private long min = Long.MAX_VALUE;
    private long count;

    public MonitoredEntry(String className, String signature) {
        this.className = className;
        this.signature = signature;
    }

    public long getAll() {
        return this.all;
    }

    public long getCount() {
        return this.count;
    }

    public long getMax() {
        return this.max;
    }

    public long getMin() {
        return this.min;
    }

    public String getClassName() {
        return this.className;
    }

    public String getSignature() {
        return this.signature;
    }

    @Override
    public String toString() {
        return "MonitorEntry{" +
                "all=" + this.all +
                ", max=" + this.max +
                ", min=" + this.min +
                ", count=" + this.count +
                '}';
    }

    public synchronized void addTime(long time) {
        this.count++;
        if (time < this.min) {
            this.min = time;
        }
        if (time > this.max) {
            this.max = time;
        }
        this.all += time;
    }

}
