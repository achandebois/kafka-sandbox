package com.happn.poc.kafka.web;

import com.happn.poc.kafka.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class EventsController {

    Flux<Event> eventFlux;

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    Flux<ServerSentEvent<Event>> events() {
        return eventFlux.map(event -> ServerSentEvent.<Event>builder()
                .data(event)
                .build()
        );
    }
}
