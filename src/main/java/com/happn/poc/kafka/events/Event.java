package com.happn.poc.kafka.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, property = Event.TYPE_PROPERTY_NAME, include = EXISTING_PROPERTY, defaultImpl = UnknownEvent.class)
@JsonSubTypes({
        @Type(value = UserCreated.class, name = UserCreated.TYPE),
        @Type(value = UserDeleted.class, name = UserDeleted.TYPE),
        @Type(value = CharmReceived.class, name = CharmReceived.TYPE),
        @Type(value = CharmCleared.class, name = CharmCleared.TYPE),
        @Type(value = UnknownEvent.class, name = UnknownEvent.TYPE),
})
public interface Event {

    String TYPE_PROPERTY_NAME = "type";

    String getType();

    String getId();

    Instant getOccurrenceTime();
}
