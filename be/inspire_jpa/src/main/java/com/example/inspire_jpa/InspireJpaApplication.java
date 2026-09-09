package com.example.inspire_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class InspireJpaApplication {

	public static void main(String[] args) {
		
		// .env 설정을 .yml 에서 참조할 수 있도록 추가하는 코드
		Dotenv env = Dotenv.configure().ignoreIfMissing().load() ; 				
		env.entries().forEach( entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
			System.out.println("debug >>>> env : "+entry.getKey()+"\t"+entry.getValue()); 
		});
		
		SpringApplication.run(InspireJpaApplication.class, args);
	}

}
