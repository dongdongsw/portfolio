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
    public PasswordEncoder pawordssEncoder() {     //패스 워드 암호화 관련 메소드
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()   //api 개발할 때는 dusable로 해놓고 꺼놓고 실제 개발시에는 enable로 수정
                .authorizeHttpRequests() //경로에 어떤 권한을 줄지 설정
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
                        "/api/post",
                        "/api/post/{id}",
                        "/api/posts/{postId}/comment/view",
                        "/api",
                        "/css/**",
                        "/js/**",
                        "/images/**").permitAll() //이 경로는 누구나 접근 허용이 가능(로그인 하지 않은 사용자들도 인증없이)
                .anyRequest().authenticated() //그 외에는 모든 접근은 로그인된 사용자만 허락함

                .and() //authorizeHttpRequests()의 설정을 마무리하고 다음으로

//                .formLogin()
//                    .loginPage("/api/user/login") //로그인 입력하는 페이지
//                    .loginProcessingUrl("/api/user/login") //로그인 처리하는 페이지
//                    .defaultSuccessUrl("/api/") //로그인 성공하면 홈으로 이동
//                    .failureUrl("/api/user/login?error=true") //로그인 실패시 이동되는 경로
                .httpBasic().disable()
                .formLogin().disable()
//                .and()//formLogi n()의 설정을 마무리하고 다음으로
                .logout().disable();       // 선택적으로 로그아웃도 비활성화

//                .logout()
//                    .logoutUrl("/api/user/logout") //로그아웃 처리하는 페이지
//                    .logoutSuccessUrl("/api/user/logout"); //로그아웃 성공하면 이동하는 경로
//                     .invalidateHttpSession(true) //


        return http.build(); //위의 설정들을 SpringSecurity가 사용할 수 있도록 반환
    }
}