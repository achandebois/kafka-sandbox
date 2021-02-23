package com.happn.poc.kafka.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.kafka.common.serialization.Serdes.serdeFrom;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KafkaJsonSerdeFactory {

    ObjectMapper mapper;

    <T> Deserializer<T> deserializer(Class<T> clazz) {
        return new JsonSerde<>(clazz, mapper).deserializer(); //NOSONAR
    }

    public <T> Serializer<T> serializer(Class<T> clazz) {
        return new JsonSerde<>(clazz, mapper).serializer(); //NOSONAR
    }

    public <T> Serde<T> serde(Class<T> clazz) {
        return serdeFrom(serializer(clazz), deserializer(clazz));
    }
}
