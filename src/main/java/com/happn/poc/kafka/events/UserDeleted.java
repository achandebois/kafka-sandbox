package com.happn.poc.kafka.events;

import lombok.Value;

import java.time.Instant;

@Value
public class UserDeleted implements UserEvent {
    static final String TYPE = "UserDeleted";

    String id;
    Instant occurrenceTime;

    String userId;

    @Override
    public String getType() {
        return TYPE;
    }

}
