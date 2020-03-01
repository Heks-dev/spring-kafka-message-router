package com.hal.kafka;

import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@KafkaHandler
public @interface KafkaMessageHandler {

    @AliasFor(annotation = KafkaHandler.class, attribute = "isDefault")
    boolean isDefault() default false;
    int partition();
}
