package com.happn.poc.kafka;

import com.happn.poc.kafka.events.Event;

public interface Publisher<T extends Event> {

    void publish(String key, T event);

    default void publish(T event) {
        publish(event.getId(), event);
    }

    default void publishTombstone(String key) {
        publish(key, null);
    }
}
