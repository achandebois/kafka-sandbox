package com.happn.poc.kafka.events;

import lombok.Value;

import java.time.Instant;

@Value
public class UserCreated implements UserEvent {
    static final String TYPE = "UserCreated";

    String id;
    Instant occurrenceTime;

    String userId;
    String userName;

    @Override
    public String getType() {
        return TYPE;
    }

}
