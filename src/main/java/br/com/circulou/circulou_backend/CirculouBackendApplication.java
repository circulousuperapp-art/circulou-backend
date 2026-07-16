package br.com.circulou.circulou_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CirculouBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CirculouBackendApplication.class, args);
	}

}
