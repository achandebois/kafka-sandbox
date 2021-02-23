package com.happn.poc.kafka.infrastructure;


import com.happn.poc.kafka.events.Event;
import com.happn.poc.kafka.events.UnknownEvent;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

public interface KafkaEventRecordFilterStrategy extends RecordFilterStrategy<String, Event> {

    static KafkaEventRecordFilterStrategy unknownEventFilter() {
        return consumerRecord -> consumerRecord.value() instanceof UnknownEvent;
    }
}
