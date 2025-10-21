package edu.miu.blog.app;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@EnableKafka
@Slf4j
public class MiuBlogBackendApplication implements CommandLineRunner {
	@Value("localhost:9092")
	private String bootstrapServers;



	public static void main(String[] args) {
		log.info("Starting MIU Blog Backend Application");
		log.debug("Classpath: {}", System.getProperty("java.class.path"));
		SpringApplication.run(MiuBlogBackendApplication.class, args);
		log.info("MIU Blog Backend Application started successfully");
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("🔌 Testing Kafka connection to " + bootstrapServers);
		try (AdminClient admin = AdminClient.create(Map.of("bootstrap.servers", bootstrapServers))) {
			System.out.println("✅ Kafka reachable, topics: " + admin.listTopics().names().get());
		} catch (Exception e) {
			System.err.println("❌ Cannot reach Kafka: " + e.getMessage());
		}
	}
}
