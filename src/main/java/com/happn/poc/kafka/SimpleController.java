package com.happn.poc.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleController {

    private final SimplePublisher simplePublisher;

    @GetMapping
    public void hello(){
        simplePublisher.publish("toto");
    }
}
