package com.happn.poc.kafka.events;

public interface UserEvent extends Event {

    String getUserId();

}
