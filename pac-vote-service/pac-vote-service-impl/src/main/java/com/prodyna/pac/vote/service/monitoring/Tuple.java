package com.prodyna.pac.vote.service.monitoring;

import java.util.Objects;

/**
 * Tuple.
 *
 * @param <K> key class.
 * @param <V> value class.
 */
public class Tuple<K, V> {

    private K key;

    private V value;

    /**
     * default constructor.
     */
    public Tuple() {
    }

    /**
     * @param key   first value.
     * @param value second value
     */
    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return key from tuple.
     */
    public K getKey() {
        return this.key;
    }

    /**
     * @param key new key.
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * @return value from tuple.
     */
    public V getValue() {
        return this.value;
    }

    /**
     * @param value new value.
     */
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(this.key, tuple.key) && Objects.equals(this.value, tuple.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "key=" + this.key +
                ", value=" + this.value +
                '}';
    }


}
