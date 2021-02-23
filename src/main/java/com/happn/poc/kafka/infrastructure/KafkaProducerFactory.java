package com.happn.poc.kafka.infrastructure;

import com.happn.poc.kafka.events.Event;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerFactory {

    KafkaJsonSerdeFactory kafkaJsonSerdeFactory;
    Supplier<String> bootstrapServerSupplier;

    public <T extends Event> ProducerFactory<String, T> producerFactory(Class<T> clazz) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        DefaultKafkaProducerFactory<String, T> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        producerFactory.setBootstrapServersSupplier(bootstrapServerSupplier);
        producerFactory.setValueSerializer(kafkaJsonSerdeFactory.serializer(clazz));
        return producerFactory;
    }
}
