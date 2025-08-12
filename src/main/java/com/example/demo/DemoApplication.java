package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // 추가

@EnableJpaAuditing // 이 어노테이션 추가
@SpringBootApplication
public class DemoApplication { // 여러분의 메인 클래스 이름에 맞게 조정하세요.

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}