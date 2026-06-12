package com.example.academic_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public NewTopic studentEnrolledTopic() {
        return TopicBuilder.name("student-enrolled-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
