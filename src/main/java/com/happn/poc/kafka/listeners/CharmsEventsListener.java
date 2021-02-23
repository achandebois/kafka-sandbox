package com.happn.poc.kafka.listeners;

import com.happn.poc.kafka.configuration.KafkaTopicConfiguration;
import com.happn.poc.kafka.events.CharmCleared;
import com.happn.poc.kafka.events.CharmReceived;
import com.happn.poc.kafka.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import reactor.core.publisher.Sinks;

import static lombok.AccessLevel.PRIVATE;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

@Slf4j
@KafkaListener(topics = KafkaTopicConfiguration.Topics.charm)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
class CharmsEventsListener {

    Sinks.Many<Event> eventPublisher;

    @KafkaHandler
    void handle(CharmReceived charmReceived) {
        log.info("{}", charmReceived);
        eventPublisher.emitNext(charmReceived, FAIL_FAST);
    }

    @KafkaHandler
    void handle(CharmCleared charmCleared) {
        log.info("{}", charmCleared);
        eventPublisher.emitNext(charmCleared, FAIL_FAST);
    }
}
