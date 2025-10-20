package edu.miu.blog.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class MiuBlogBackendApplication {

	public static void main(String[] args) {
		log.info("Starting MIU Blog Backend Application");
		log.debug("Classpath: {}", System.getProperty("java.class.path"));
		SpringApplication.run(MiuBlogBackendApplication.class, args);
		log.info("MIU Blog Backend Application started successfully");
	}
}
