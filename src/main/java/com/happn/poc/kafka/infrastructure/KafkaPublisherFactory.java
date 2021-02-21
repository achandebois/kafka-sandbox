package com.happn.poc.kafka.infrastructure;

import com.happn.poc.kafka.Publisher;
import com.happn.poc.kafka.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class KafkaPublisherFactory {

    KafkaProducerFactory kafkaProducerFactory;

    public <T extends Event> Publisher<T> singleTopicPublisher(Class<T> clazz, String topic) {
        KafkaTemplate<String, T> template = new KafkaTemplate<>(kafkaProducerFactory.producerFactory(clazz));
        template.setDefaultTopic(topic);
        return template::sendDefault;
    }
}
