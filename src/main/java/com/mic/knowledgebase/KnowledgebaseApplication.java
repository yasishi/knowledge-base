package com.mic.knowledgebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import com.mic.knowledgebase.config.CorsConfig;

@SpringBootApplication
@Import(CorsConfig.class)
public class KnowledgebaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowledgebaseApplication.class, args);
	}
}
