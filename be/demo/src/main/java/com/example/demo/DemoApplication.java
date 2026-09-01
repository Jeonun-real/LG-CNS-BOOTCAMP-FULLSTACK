package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

//framework에서 위임된 객체 -> bean 표시
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DemoApplication {

	/*
	npm start (react)
	서버에 베포가 된다는건 packing 이 된다는거
	build - ./gradle bootjar - xxxx.jar
	deploy = staert tomcat server -(xxxx.jar)
	*/
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
