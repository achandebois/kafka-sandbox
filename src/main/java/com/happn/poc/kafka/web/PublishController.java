package com.happn.poc.kafka.web;

import com.happn.poc.kafka.Publisher;
import com.happn.poc.kafka.events.CharmReceived;
import com.happn.poc.kafka.events.Event;
import com.happn.poc.kafka.events.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/publish")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class PublishController {

    Publisher<UserEvent> userEventsPublisher;
    Publisher<CharmReceived> charmReceivedPublisher;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    Mono<Void> publish(@RequestBody Event event) {
        internalPublish(event);
        return Mono.empty();
    }

    private void internalPublish(Event event) {
        if (event instanceof UserEvent) {
            userEventsPublisher.publish((UserEvent) event);
            return;
        }
        charmReceivedPublisher.publish((CharmReceived) event);
    }
}
