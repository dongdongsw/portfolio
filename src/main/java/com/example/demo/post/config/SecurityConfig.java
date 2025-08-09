package com.example.demo.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (개발 중 편의를 위함)
                .authorizeHttpRequests(authorize -> authorize
                        // API 경로 허용
                        .requestMatchers("/api/**").permitAll()
                        // HTML 파일 허용
                        .requestMatchers("/upload_db.html").permitAll()
                        // 이미지 접근 경로 허용 (WebConfig와 일치)
                        .requestMatchers("/images/**").permitAll() // <<< 다시 추가!

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}