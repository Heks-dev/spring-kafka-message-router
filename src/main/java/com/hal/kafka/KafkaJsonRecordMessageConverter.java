package com.hal.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KafkaJsonRecordMessageConverter implements RecordMessageConverter {

    private final TypeResolver<KafkaRoute> resolver;
    private final ObjectMapper mapper;

    public KafkaJsonRecordMessageConverter(final TypeResolver<KafkaRoute> resolver, final ObjectMapper mapper) {
        this.resolver = resolver;
        this.mapper = mapper;
    }

    @Override
    public Message<?> toMessage(
            final ConsumerRecord<?, ?> record,
            final Acknowledgment acknowledgment,
            final Consumer<?, ?> consumer,
            final Type payloadType
    ) {
        Map<String, String> headers = Arrays.stream(record.headers().toArray())
                .collect(Collectors.toMap(Header::key, header -> new String(header.value())));
        Type type = resolver.resolve(new KafkaRoute(record.topic(), record.partition()));
        Object payload = deserialize(record.value(), type);
        if (Objects.isNull(payload)) {
            throw new MessageConversionException("Message body is empty.");
        }
        return MessageBuilder.withPayload(payload)
                .copyHeaders(headers)
                .build();
    }

    @Override
    public ProducerRecord<?, ?> fromMessage(final Message<?> message, final String defaultTopic) {
        MessageHeaders headers = message.getHeaders();
        String topic = resolveTopic(headers.get(KafkaHeaders.TOPIC, String.class), defaultTopic);
        Integer partition = headers.get(KafkaHeaders.PARTITION_ID, Integer.class);
        Object key = headers.get(KafkaHeaders.MESSAGE_KEY);
        Long timestamp = headers.get(KafkaHeaders.TIMESTAMP, Long.class);
        String jsonValue = writeValueAsString(message.getPayload());
        Headers producerHeaders = constructHeaders(headers);
        return new ProducerRecord<>(
                topic,
                partition,
                timestamp,
                key,
                jsonValue,
                producerHeaders
        );
    }

    private String writeValueAsString(final Object payload) {
        if (payload == null) {
            return null;
        }
        if (payload instanceof String) {
            return (String) payload;
        }
        try {
            return mapper.writeValueAsString(payload);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize kafka message payload to json");
        }
    }

    private Headers constructHeaders(final MessageHeaders headers) {
        // TODO: implement
        return new RecordHeaders();
    }

    private String resolveTopic(String headerTopic, String defaultTopic) {
        return headerTopic == null ? defaultTopic : headerTopic;
    }

    private Object deserialize(final Object payload, final Type type) {
        if (payload == null) {
            return null;
        }
        try {
            PreciseTypeReference<?> typeReference = new PreciseTypeReference<>(type);
            if (payload instanceof byte[]) {
                byte[] bytes = (byte[]) payload;
                return mapper.readValue(bytes, typeReference);
            }
            if (payload instanceof String) {
                return mapper.readValue((String) payload, typeReference);
            }
            throw new IncompatibleTypeException();
        } catch (IOException e) {
            throw new IncompatibleTypeException(e);
        }
    }
}
