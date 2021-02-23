package com.happn.poc.kafka.events;

import lombok.Value;

import java.time.Instant;

import static com.happn.poc.kafka.events.CharmEvent.CharmAggregator.charmReceived;

@Value
public class CharmReceived implements CharmEvent {
    static final String TYPE = "CharmReceived";

    String id;
    Instant occurrenceTime;

    String userId;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public CharmAggregator aggregator() {
        return charmReceived();
    }
}
