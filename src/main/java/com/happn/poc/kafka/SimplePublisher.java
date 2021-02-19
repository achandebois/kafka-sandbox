package com.happn.poc.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimplePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(String msg){
        kafkaTemplate.send("test", msg);
    }
}
