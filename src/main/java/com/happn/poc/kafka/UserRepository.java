package com.happn.poc.kafka;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> user(String userId);

    Flux<User> users();
}
