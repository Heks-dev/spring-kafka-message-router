package com.hal.kafka;

import java.lang.reflect.Type;
import java.util.Optional;

public interface MessageTypeFactory {

    Optional<Type> get(Route route);
    void put(Route route, Type type);
}
