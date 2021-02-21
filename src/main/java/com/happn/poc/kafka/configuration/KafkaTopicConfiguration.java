package com.happn.poc.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    NewTopic userTopic() {
        return TopicBuilder.name(Topics.user)
                .partitions(10)
                .replicas(3)
                .build();
    }

    @Bean
    NewTopic charmTopic() {
        return TopicBuilder.name(Topics.charm)
                .partitions(10)
                .replicas(3)
                .build();
    }

    public static final class Topics {
        public static final String user = "user";
        public static final String charm = "charm";
    }

    public static final class Store {
        public static final String charms = "charms";
        public static final String users = "users";
        public static final String usersWithCharms = "users-with-charms";
    }

    public static final class ApplicationId {
        public static final String defaultApplicationId = "happn-streams-id";
    }
}
