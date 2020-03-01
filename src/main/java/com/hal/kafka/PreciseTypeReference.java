package com.hal.kafka;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

public class PreciseTypeReference<T> extends TypeReference<T> {

    private final Type type;

    public PreciseTypeReference(final Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }
}
