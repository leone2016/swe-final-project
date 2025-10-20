package edu.miu.blog.app.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.miu.blog.app.domain.Article;


@Service
public class ArticleConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @KafkaListener(
        topics = "${app.kafka.topic}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(Article event) {
        System.out.println("📩 Received article event: " + event);

        sendEmailNotification(event);
    }

    private void sendEmailNotification(Article event) {
        String subject = "📝 New Article Published: " + event.getTitle();
        String body = """
                Hi %s,
                
                Your article "%s" was successfully published at %s.
                
                🔍 Preview:
                %s
                
                You can view it on your profile soon!
                
                — Blog Platform Team
                """.formatted(event.getAuthor().getUsername(), event.getTitle(), event.getCreatedAt(), event.getDescription());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getAuthor().getEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("📧 Email sent to " + event.getAuthor().getEmail());
    }
}
