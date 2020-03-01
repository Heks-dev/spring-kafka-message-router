package com.hal.kafka;

import java.lang.reflect.Type;

public interface TypeResolver<R extends Route> {

    Type resolve(R route);
}
