package com.hal.kafka;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMessageTypeFactory implements MessageTypeFactory {

    private Map<Route, Type> typeMap = new HashMap<>();

    @Override
    public Optional<Type> get(final Route route) {
        return Optional.ofNullable(typeMap.get(route));
    }

    public void put(final Route route, final Type type) {
        typeMap.put(route, type);
    }
}
