package edu.miu.blog.app.kafka;

import edu.miu.blog.app.dto.article.ArticleEventDTO;
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

    public void sendArticleCreatedEvent(Article article) {
        ArticleEventDTO event = new ArticleEventDTO(
                article.getTitle(),
                article.getDescription(),
                article.getAuthor().getUsername(),
                article.getAuthor().getEmail(),
                article.getCreatedAt()
        );
        kafkaTemplate.send(topicName, event);
        System.out.println("✅ Sent Kafka event to topic [" + topicName + "]: " + event);

    }
}
