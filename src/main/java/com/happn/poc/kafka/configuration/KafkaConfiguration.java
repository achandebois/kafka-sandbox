package com.happn.poc.kafka.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happn.poc.kafka.events.Event;
import com.happn.poc.kafka.infrastructure.KafkaConsumerFactory;
import com.happn.poc.kafka.infrastructure.KafkaJsonSerdeFactory;
import com.happn.poc.kafka.infrastructure.KafkaProducerFactory;
import com.happn.poc.kafka.infrastructure.KafkaPublisherFactory;
import com.happn.poc.kafka.infrastructure.KafkaRepeatableWithBackOffLoggingErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.stream.Collectors;

import static com.happn.poc.kafka.infrastructure.KafkaEventRecordFilterStrategy.unknownEventFilter;
import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
class KafkaConfiguration implements KafkaListenerConfigurer {

    static long backoffIntervalInMilliseconds = 50;
    static int backoffMaxAttempts = 3;

    @Bean
    ConsumerFactory<String, Event> notificationEventConsumerFactory(KafkaConsumerFactory kafkaConsumerFactory) {
        return kafkaConsumerFactory.createMessageConsumerFactory(Event.class);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Event> kafkaListenerContainerFactory(ConsumerFactory<String, Event> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Event> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setErrorHandler(new KafkaRepeatableWithBackOffLoggingErrorHandler(new FixedBackOff(backoffIntervalInMilliseconds, backoffMaxAttempts)));
        factory.setRecordFilterStrategy(unknownEventFilter());
        return factory;
    }

    @Bean
    KafkaConsumerFactory kafkaHappnConsumerFactory(
            KafkaJsonSerdeFactory kafkaJsonSerdeFactory,
            KafkaProperties kafkaProperties
    ) {
        String groupId = kafkaProperties.getConsumer().getGroupId();
        List<String> bootstrapServers = kafkaProperties.getBootstrapServers();
        return new KafkaConsumerFactory(groupId, kafkaJsonSerdeFactory, () -> String.join(",", bootstrapServers));
    }


    @Bean
    KafkaProducerFactory kafkaHappnProducerFactory(
            KafkaJsonSerdeFactory kafkaJsonSerdeFactory,
            KafkaProperties kafkaProperties) {
        List<String> bootstrapServers = kafkaProperties.getBootstrapServers();
        return new KafkaProducerFactory(kafkaJsonSerdeFactory, () -> String.join(",", bootstrapServers));
    }

    @Bean
    KafkaPublisherFactory kafkaPublisherFactory(KafkaProducerFactory kafkaProducerFactory) {
        return new KafkaPublisherFactory(kafkaProducerFactory);
    }

    @Bean
    KafkaJsonSerdeFactory kafkaJsonSerdeFactory(ObjectMapper mapper) {
        return new KafkaJsonSerdeFactory(mapper);
    }

    @Bean
    Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setValidator(validator());
    }


}
