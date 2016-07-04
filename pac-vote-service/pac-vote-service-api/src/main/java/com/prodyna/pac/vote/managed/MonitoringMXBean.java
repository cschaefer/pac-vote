package com.prodyna.pac.vote.managed;


public interface MonitoringMXBean {

    String print();

    void clear();

    void enabled();

    void disable();

    void registerInJMX();

    void unregisterFromJMX();

}