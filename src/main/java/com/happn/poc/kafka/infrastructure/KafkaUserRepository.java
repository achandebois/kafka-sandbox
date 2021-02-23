package com.happn.poc.kafka.infrastructure;

import com.happn.poc.kafka.User;
import com.happn.poc.kafka.UserRepository;
import com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Store;
import com.happn.poc.kafka.web.UserNotFound;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class KafkaUserRepository implements UserRepository {

    StreamsBuilderFactoryBean streams;

    @Override
    public Mono<User> user(String userId) {
        return Mono.defer(() -> Mono.just(stateStore().orElseThrow())
                .flatMap(store -> Mono.justOrEmpty(store.get(userId)))
                .switchIfEmpty(Mono.error(new UserNotFound())));
    }

    @Override
    public Flux<User> users() {
        return Flux.defer(() -> Flux
                .fromStream(StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(
                                stateStore().orElseThrow().all(), Spliterator.ORDERED), false))
                .map(store -> store.value)
        );
    }

    private Optional<ReadOnlyKeyValueStore<String, User>> stateStore() {
        KafkaStreams kafkaStreams = streams.getKafkaStreams();
        if (kafkaStreams.state() != RUNNING) {
            return Optional.empty();
        }
        return Optional.of(kafkaStreams
                .store(Store.usersWithCharms, QueryableStoreTypes.keyValueStore()));
    }
}
