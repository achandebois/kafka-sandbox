package com.happn.poc.kafka.events;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

import static com.happn.poc.kafka.events.CharmEvent.CharmAggregator.charmCleared;

@Value
@Builder
public class CharmCleared implements CharmEvent {
    static final String TYPE = "CharmCleared";

    String id;
    Instant occurrenceTime;

    String userId;

    @Override
    public String getType() {
        return TYPE;
    }

    public static CharmCleared forUser(String userId) {
        return CharmCleared.builder()
                .id(UUID.randomUUID().toString())
                .occurrenceTime(Instant.now())
                .userId(userId)
                .build();
    }

    @Override
    public CharmAggregator aggregator() {
        return charmCleared();
    }
}
