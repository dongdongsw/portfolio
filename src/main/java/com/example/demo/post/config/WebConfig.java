package com.example.demo.post.config; // SecurityConfig와 같은 패키지에 두는 것을 추천

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 이미지가 저장될 실제 디렉토리 경로 (PostService의 uploadDir과 동일해야 함)
    private String uploadDir = "C:/tmp/images/"; // PostService와 일치시켜 주세요!

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**") // '/images/'로 시작하는 모든 요청을 처리
                .addResourceLocations("file:///" + uploadDir); // 실제 파일이 있는 디렉토리
        // 윈도우 경로인 C:/tmp/images/ 이므로 file:///C:/tmp/images/ 처럼 'file:///' 접두사 필요
    }
}