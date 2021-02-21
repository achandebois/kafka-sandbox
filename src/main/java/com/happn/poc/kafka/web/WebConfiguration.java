package com.happn.poc.kafka.web;

import com.happn.poc.kafka.Publisher;
import com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Topics;
import com.happn.poc.kafka.events.CharmReceived;
import com.happn.poc.kafka.events.Event;
import com.happn.poc.kafka.events.UserEvent;
import com.happn.poc.kafka.infrastructure.KafkaPublisherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
class WebConfiguration {

    @Bean
    Flux<Event> eventFlux(Sinks.Many<Event> eventSink) {
        return eventSink.asFlux();
    }

    @Bean
    Publisher<UserEvent> userEventsPublisher(KafkaPublisherFactory publisherFactory) {
        return publisherFactory.singleTopicPublisher(UserEvent.class, Topics.user);
    }

    @Bean
    Publisher<CharmReceived> charmReceivedPublisher(KafkaPublisherFactory publisherFactory) {
        return publisherFactory.singleTopicPublisher(CharmReceived.class, Topics.charm);
    }
}
