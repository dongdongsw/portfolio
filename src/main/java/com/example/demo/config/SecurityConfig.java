package com.example.demo.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   //api 개발할 때는 dusable로 해놓고 꺼놓고 실제 개발시에는 enable로 수정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth //경로에 어떤 권한을 줄지 설정
                .requestMatchers(
                        "/api/comments/post/**",
                        "/api/comments/singleview/**",
                        "/api/comments/edit/**",
                        "/api/comments/delete/**"
                        ).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(
//                            --------------------------로그인
                        "/api/user/create",
                        "/api/user/login",
                        "/api/user/findid/send-auth",
                        "/api/user/findid/verify-id",
                        "/api/user/findpw/send-auth",
                        "/api/user/findpw/verify-pw",
                        "/api/user/logout",
                        //---------------------------
                        "/api/mypage/**",
                        //---------------------------
                        "/api/posts",
                        "/api/posts/detail/**",
                        "/api/posts/modify/**",
                        "/api/posts/delete/**",
                        "/api/post/{id}",
                        "/api/posts/{postId}/comment/view",
                        //---------------------------
                        "/api",
                        "/css/**",
                        "/js/**",
                        "/images/**")
                .permitAll() //이 경로는 누구나 접근 허용이 가능(로그인 하지 않은 사용자들도 인증없이)
                .anyRequest().authenticated() //그 외에는 모든 접근은 로그인된 사용자만 허락함
                )
//                .formLogin()
//                    .loginPage("/api/user/login") //로그인 입력하는 페이지
//                    .loginProcessingUrl("/api/user/login") //로그인 처리하는 페이지
//                    .defaultSuccessUrl("/api/") //로그인 성공하면 홈으로 이동
//                    .failureUrl("/api/user/login?error=true") //로그인 실패시 이동되는 경로
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션이 필요할 때 생성 (기본값)
                        .sessionFixation().migrateSession() // 세션 고정 공격 방어 (세션 ID 변경)
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
//                .and()//formLogi n()의 설정을 마무리하고 다음으로
                .logout(logout -> logout.disable());       // 선택적으로 로그아웃도 비활성화

//                .logout()
//                    .logoutUrl("/api/user/logout") //로그아웃 처리하는 페이지
//                    .logoutSuccessUrl("/api/user/logout"); //로그아웃 성공하면 이동하는 경로
//                     .invalidateHttpSession(true) //


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