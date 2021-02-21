package com.happn.poc.kafka.configuration;

import com.happn.poc.kafka.User;
import com.happn.poc.kafka.UserRepository;
import com.happn.poc.kafka.configuration.KafkaTopicConfiguration.ApplicationId;
import com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Store;
import com.happn.poc.kafka.events.CharmEvent;
import com.happn.poc.kafka.events.UserEvent;
import com.happn.poc.kafka.infrastructure.KafkaJsonSerdeFactory;
import com.happn.poc.kafka.infrastructure.KafkaStreamsLogExceptionHandler;
import com.happn.poc.kafka.infrastructure.KafkaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Store.users;
import static com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Store.usersWithCharms;
import static com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Topics.charm;
import static com.happn.poc.kafka.configuration.KafkaTopicConfiguration.Topics.user;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.OPTIMIZE;
import static org.apache.kafka.streams.StreamsConfig.REPLICATION_FACTOR_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.TOPOLOGY_OPTIMIZATION;
import static org.apache.kafka.streams.kstream.Consumed.with;
import static org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME;

@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
class KafkaUsersStreamsConfiguration {

    KafkaJsonSerdeFactory serdeFactory;

    @Bean(name = DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamsConfiguration(KafkaProperties kafkaProperties) {
        List<String> bootstrapServers = kafkaProperties.getBootstrapServers();
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, ApplicationId.defaultApplicationId);
        props.put(BOOTSTRAP_SERVERS_CONFIG, String.join(",", bootstrapServers));
        props.put(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getCanonicalName());
        props.put(DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, KafkaStreamsLogExceptionHandler.class.getCanonicalName());
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(REPLICATION_FACTOR_CONFIG, 3);
        props.put(COMMIT_INTERVAL_MS_CONFIG, 100L);
        props.put(TOPOLOGY_OPTIMIZATION, OPTIMIZE);
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    KTable<String, User> users(StreamsBuilder streamsBuilder) {
        KTable<String, Long> charms = streamsBuilder.stream(charm, consumedWith(CharmEvent.class))
                .selectKey((key, value) -> value.getUserId())
                .groupByKey()
                .aggregate(
                        CharmEvent::initializer,
                        (userId, charmEvent, userCharms) -> charmEvent.aggregator().aggregate(userCharms),
                        materializedAs(Long.class, Store.charms));

        return streamsBuilder.stream(user, consumedWith(UserEvent.class))
                .selectKey((key, value) -> value.getUserId())
                .groupByKey()
                .aggregate(
                        User::empty,
                        (userId, userEvent, userState) -> userState.after(userEvent),
                        materializedAs(User.class, users))
                .leftJoin(charms, User::with, materializedAs(User.class, usersWithCharms));
    }

    @Bean
    UserRepository charmsRepository(StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        return new KafkaUserRepository(streamsBuilderFactoryBean);
    }

    private <T> Consumed<String, T> consumedWith(Class<T> clazz) {
        return with(Serdes.String(), serdeFactory.serde(clazz));
    }

    private <T> Materialized<String, T, KeyValueStore<Bytes, byte[]>> materializedAs(Class<T> clazz, String storeName) {
        return Materialized.<String, T>as(Stores.inMemoryKeyValueStore(storeName))
                .withKeySerde(Serdes.String())
                .withValueSerde(serdeFactory.serde(clazz));
    }
}
