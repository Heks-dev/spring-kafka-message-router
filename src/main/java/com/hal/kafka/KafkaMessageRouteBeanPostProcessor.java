package com.hal.kafka;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class KafkaMessageRouteBeanPostProcessor implements BeanPostProcessor {

    private final MessageTypeFactory typeFactory;
    private final Environment environment;

    public KafkaMessageRouteBeanPostProcessor(final MessageTypeFactory typeFactory, final Environment environment) {
        this.typeFactory = typeFactory;
        this.environment = environment;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(KafkaListener.class)) {
            KafkaListener listenerAnnotation = beanClass.getAnnotation(KafkaListener.class);
            String[] topics = listenerAnnotation.topics();
            if (topics.length != 1) {
                throw new IllegalArgumentException(String.format(
                        "Unable register route due to the declaration of %s " +
                                "listener contains more than one topics", beanName));
            }
            String topicName = resolvePlacedValue(topics[0]);
            for (Method declaredMethod : beanClass.getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(KafkaMessageHandler.class)) {
                    KafkaMessageHandler handlerAnnotation = declaredMethod.getAnnotation(KafkaMessageHandler.class);
                    for (Parameter parameter : declaredMethod.getParameters()) {
                        if (parameter.isAnnotationPresent(Payload.class)) {
                            typeFactory.put(new KafkaRoute(topicName, handlerAnnotation.partition()),
                                    parameter.getParameterizedType());
                        }
                    }
                }
            }
        }
        return bean;
    }

    private String resolvePlacedValue(final String placedValue) {
        if (placedValue.matches("\\$\\{.*}")) {
            return environment.resolveRequiredPlaceholders(placedValue);
        }
        return placedValue;
    }
}
