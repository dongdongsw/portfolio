/*
package com.example.demo.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 꺼줌
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // /api/** 모든 요청 인증 없이 허용
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
*/
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
                        // /api/** 경로에 대한 접근 허용 (게시글 API 엔드포인트)
                        .requestMatchers("/api/**").permitAll()

                        // upload_db.html 파일에 대한 접근 허용
                        .requestMatchers("/upload_db.html").permitAll()

                        // *** 중요 수정: /images/** 경로에 대한 접근 허용 추가 ***
                        .requestMatchers("/images/**").permitAll() // <-- 이 라인이 꼭 필요합니다!

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}