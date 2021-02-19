package com.happn.poc.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleConsumer {

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${kafka.consumer.group.id}")
    public void receive(String msg){
        System.out.println("hello ");
        System.out.println(msg);
    }
}
