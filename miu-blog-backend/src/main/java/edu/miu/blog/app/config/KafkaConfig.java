package edu.miu.blog.app.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
@Slf4j
public class KafkaConfig {
    @Value("${app.kafka.topic}")
    private String topicName;
    
    @Bean
    public NewTopic articleTopic() {
        log.info(" 🟢Configuring Kafka topic: {}", topicName);
        return TopicBuilder.name(topicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
