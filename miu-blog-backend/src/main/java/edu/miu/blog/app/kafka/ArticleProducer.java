package edu.miu.blog.app.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import edu.miu.blog.app.domain.Article;



@Service
public class ArticleProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;

    public ArticleProducer(KafkaTemplate<String, Object> kafkaTemplate,
    @Value("${app.kafka.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendArticleCreatedEvent(Article event) {
        kafkaTemplate.send(topicName, event);
        System.out.println("✅ Sent Kafka event to topic [" + topicName + "]: " + event);

    }
}
