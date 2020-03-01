package com.hal.kafka;

import java.util.Objects;

public class KafkaRoute implements Route {

    private final String destination;
    private final int partition;

    public KafkaRoute(final String destination, final int partition) {
        this.destination = destination;
        this.partition = partition;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public int getPartition() {
        return partition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KafkaRoute that = (KafkaRoute) o;
        return partition == that.partition &&
                Objects.equals(destination, that.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, partition);
    }
}
