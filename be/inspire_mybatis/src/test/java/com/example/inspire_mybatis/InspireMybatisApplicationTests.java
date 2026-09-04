package com.example.inspire_mybatis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class InspireMybatisApplicationTests {

	static {
		Dotenv.configure()
				.ignoreIfMissing()
				.load()
				.entries()
				.forEach(entry ->
						System.setProperty(entry.getKey(), entry.getValue()));
	}

	@Test
	void contextLoads() {
	}

}
