package com.hal.kafka;

import java.lang.reflect.Type;

public class KafkaTypeResolver implements TypeResolver<KafkaRoute> {

    private final MessageTypeFactory typeFactory;

    public KafkaTypeResolver(final MessageTypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    @Override
    public Type resolve(final KafkaRoute route) {
        return typeFactory.get(route)
                .orElseThrow(() -> new RouteNotFoundException(route.getDestination(), route.getPartition()));
    }
}
