package com.hal.kafka;

public class RouteNotFoundException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE =
            "Deserialization type was not found for route topic [%s] and partition [%d]";

    public RouteNotFoundException(final String topic, final int partition) {
        super(String.format(MESSAGE_TEMPLATE, topic, partition));
    }
}
