package com.happn.poc.kafka.listeners;

import com.happn.poc.kafka.Publisher;
import com.happn.poc.kafka.configuration.KafkaTopicConfiguration;
import com.happn.poc.kafka.events.CharmCleared;
import com.happn.poc.kafka.events.Event;
import com.happn.poc.kafka.events.UserCreated;
import com.happn.poc.kafka.events.UserDeleted;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import reactor.core.publisher.Sinks;

import static lombok.AccessLevel.PRIVATE;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

@Slf4j
@KafkaListener(topics = KafkaTopicConfiguration.Topics.user)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
class UserEventsListener {

    Sinks.Many<Event> eventPublisher;
    Publisher<CharmCleared> charmClearedPublisher;

    @KafkaHandler
    void handle(UserCreated userCreated) {
        log.info("{}", userCreated);
        eventPublisher.emitNext(userCreated, FAIL_FAST);
    }

    @KafkaHandler
    void handle(UserDeleted userDeleted) {
        log.info("{}", userDeleted);
        eventPublisher.emitNext(userDeleted, FAIL_FAST);
        charmClearedPublisher.publish(CharmCleared.forUser(userDeleted.getUserId()));
    }
}
