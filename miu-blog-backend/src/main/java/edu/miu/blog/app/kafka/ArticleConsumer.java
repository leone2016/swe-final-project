package edu.miu.blog.app.kafka;

import edu.miu.blog.app.dto.article.ArticleEventDTO;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.miu.blog.app.domain.Article;

import java.util.List;


@Service
@Slf4j
public class ArticleConsumer {
    @Value("${app.mail.tolken}")
    private final String TOKEN = "<NO-TOKEN>";

    @KafkaListener(
        topics = "${app.kafka.topic}"
    )
    public void consume(ArticleEventDTO event) {
        log.info("📩 Received article event: {}", event);
        sendEmailNotification(event);
    }

    private void sendEmailNotification(ArticleEventDTO event) {
        String subject = "📝 New Article Published: " + event.getTitle();
        String body = """
                Hi %s,
                
                Your article "%s" was successfully published at %s.
                
                🔍 Preview:
                %s
                
                You can view it on your profile soon!
                
                — Blog Platform Team
                """.formatted(event.getAuthorName(), event.getTitle(), event.getCreatedAt(), event.getDescription());

        try  {
            final MailtrapConfig config = new MailtrapConfig.Builder()
                    .token(TOKEN)
                    .build();

            final MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);

            final MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@demomailtrap.co", "MIU BLOG - NEW ARTICLE CREATED"))
                    .to(List.of(new Address(event.getAuthorEmail())))
                    .subject(subject)
                    .text(body)
                    .category("EA")
                    .build();

            System.out.println(client.send(mail));
            System.out.println("✅ MAIl sended: ");

        } catch (Exception e) {
            log.error("❌ Error sending email:  " + e.getMessage());
        }
    }
}
