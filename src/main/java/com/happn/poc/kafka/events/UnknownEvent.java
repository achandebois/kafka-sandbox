package com.happn.poc.kafka.events;

import lombok.Value;

import java.time.Instant;

@Value
public class UnknownEvent implements Event {
    static final String TYPE = "UnknownEvent";

    String id;
    Instant occurrenceTime;

    @Override
    public String getType() {
        return TYPE;
    }
}
