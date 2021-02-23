package com.happn.poc.kafka.listeners;

import com.happn.poc.kafka.Publisher;
import com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Topics;
import com.happn.poc.kafka.events.CharmCleared;
import com.happn.poc.kafka.events.Event;
import com.happn.poc.kafka.infrastructure.KafkaPublisherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
class ListenersConfiguration {

    @Bean
    CharmsEventsListener charmsEventsListener(Sinks.Many<Event> eventSink) {
        return new CharmsEventsListener(eventSink);
    }

    @Bean
    UserEventsListener userEventsListener(
            Sinks.Many<Event> eventSink,
            Publisher<CharmCleared> publisher
    ) {
        return new UserEventsListener(eventSink, publisher);
    }

    @Bean
    Publisher<CharmCleared> charmClearedPublisher(KafkaPublisherFactory publisherFactory) {
        return publisherFactory.singleTopicPublisher(CharmCleared.class, Topics.charm);
    }

    @Bean
    Sinks.Many<Event> eventSink() {
        return Sinks.many().multicast().directBestEffort();
    }
}
