package com.happn.poc.kafka.web;

import com.happn.poc.kafka.User;
import com.happn.poc.kafka.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/charms")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class CharmsController {

    UserRepository userRepository;

    @GetMapping(path = "/{userId}", produces = APPLICATION_JSON_VALUE)
    Mono<User> charms(@PathVariable String userId) {
        return userRepository.user(userId);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    Flux<User> charms() {
        return userRepository.users();
    }
}
