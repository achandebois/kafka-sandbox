package com.happn.poc.kafka.infrastructure;

import com.happn.poc.kafka.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES;
import static org.springframework.kafka.support.serializer.JsonDeserializer.USE_TYPE_INFO_HEADERS;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KafkaConsumerFactory {

    String group;
    KafkaJsonSerdeFactory kafkaJsonSerdeFactory;
    Supplier<String> bootstrapServerSupplier;

    private <T extends Event> Deserializer<T> deserializer(Class<T> clazz, Map<String, Object> config) {
        Deserializer<T> deserializer = kafkaJsonSerdeFactory.deserializer(clazz);
        ErrorHandlingDeserializer<T> result = new ErrorHandlingDeserializer<>(deserializer);
        deserializer.configure(config, false);
        return result;
    }

    private <T extends Event> ConsumerFactory<String, T> createFactory(Class<T> clazz, Map<String, Object> overrideConfig) {
        final Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put(GROUP_ID_CONFIG, group);
        defaultConfig.put(TRUSTED_PACKAGES, "*");
        defaultConfig.put(USE_TYPE_INFO_HEADERS, false);

        HashMap<String, Object> config = new HashMap<>(defaultConfig);
        config.putAll(overrideConfig);
        DefaultKafkaConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer(clazz, config));
        consumerFactory.setBootstrapServersSupplier(bootstrapServerSupplier);
        return consumerFactory;
    }

    public <T extends Event> ConsumerFactory<String, T> createMessageConsumerFactory(Class<T> clazz) {
        return createFactory(clazz, new HashMap<>());
    }
}
