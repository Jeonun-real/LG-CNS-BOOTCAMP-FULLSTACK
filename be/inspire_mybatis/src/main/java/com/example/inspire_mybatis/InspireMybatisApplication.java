package com.example.inspire_mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class InspireMybatisApplication {

	public static void main(String[] args) {
		
		
		// .env 설정은 .yml에서 사용할수 있게 해주는 코드
		Dotenv env = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		env.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
			System.out.println("Loaded environment variable: " + entry.getKey() + "=" + entry.getValue());

		});
		
		SpringApplication.run(InspireMybatisApplication.class, args);
	}

}
