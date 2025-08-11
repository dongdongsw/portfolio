package com.example.demo.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화 (아래 corsConfigurationSource() 사용)
                .cors(Customizer.withDefaults())
                // 개발용: CSRF 전역 비활성화 (운영에서는 /api/**만 무시하도록 바꾸는 걸 권장)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // API, 이미지, 정적 리소스/HTML 허용
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers(
                                "/", "/index.html",
                                "/post_list.html", "/post_create.html", "/post_edit.html",
                                "/posts_list.html"

                        ).permitAll()

                        // 그 외도 개발 중에는 모두 허용
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}