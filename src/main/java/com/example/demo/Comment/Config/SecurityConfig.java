package com.example.demo.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //Spring Security를 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {     //패스 워드 암호화 관련 메소드
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable())
          .cors(cors -> cors.configurationSource(corsConfigurationSource()))
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
//            --------------------------로그인
                "/api/user/create",
                "/api/user/login",
                "/api/user/findid/send-auth",
                "/api/user/findid/verify-id",
                "/api/user/findpw/send-auth",
                "/api/user/findpw/verify-pw",
                "/api/user/logout",
                // 게시글/댓글 공개 엔드포인트
                "/api/post",
                "/api/post/{id}",
                "/api/posts/{postId}/comment/view",
                "/api/comments/post/**",
                "/api/comments/singleview/**",
                "/api/comments/edit/**",
                "/api/comments/delete/**",

                "/api",
                "/css/**", "/js/**", "/images/**"
            ).permitAll() //이 경로는 누구나 접근 허용이 가능(로그인 하지 않은 사용자들도 인증없이)
            .anyRequest().authenticated() //그 외에는 모든 접근은 로그인된 사용자만 허락함
        )
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable());       // 선택적으로 로그아웃도 비활성화

        return http.build(); //위의 설정들을 SpringSecurity가 사용할 수 있도록 반환
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}