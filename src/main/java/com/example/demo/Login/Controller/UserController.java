// src/main/java/com/example/demo/Login/Controller/UserController.java

package com.example.demo.Login.Controller;

import com.example.demo.Login.Service.MailService;
import com.example.demo.Login.dto.MailRequestDto;
import com.example.demo.Login.dto.UserLoginRequestDto;
import com.example.demo.Login.dto.UserLoginResponseDto;
import com.example.demo.Login.dto.UserSignupRequestDto;
import com.example.demo.Login.Entity.UserEntity;
import com.example.demo.Login.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    @PostMapping("/create")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {
        // UserService의 signup 메소드 실행
        userService.signup(requestDto);

        // 회원가입 완료 메시지 반환
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto requestDto, HttpSession session) {
        // 1. 스프링 시큐리티 인증
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(requestDto.getLoginid(), requestDto.getLoginpw());
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. SecurityContext를 세션에 저장
        session.setAttribute(
                org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        // 3. 세션에 로그인 사용자 정보를 DTO로 저장
        UserEntity userEntity = userService.getUserByLoginId(requestDto.getLoginid());
        UserLoginResponseDto responseDto = new UserLoginResponseDto(
                userEntity.getLoginid(),
                userEntity.getEmail(), // ✅ userEntity에서 이메일 값 할당
                userEntity.getNickName(),
                userEntity.getPhone(),
                userEntity.getBirthday(),
                userEntity.getLocation(),
                userEntity.getImagePath()
        );

        // 세션에 loginUser 키로 저장
        session.setAttribute("loginUser", responseDto);
        session.setMaxInactiveInterval(1800); // 세션 30분

        return ResponseEntity.ok("로그인이 완료되었습니다");
    }

    // 로그인 세션 확인용 API
    @GetMapping("/session-info")
    public ResponseEntity<UserLoginResponseDto> getSessionInfo(HttpSession session) {
        UserLoginResponseDto loginUser = (UserLoginResponseDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.badRequest().body(null); // 로그인 세션이 없으면 에러
        }
        return ResponseEntity.ok(loginUser); // JSON 형태로 세션 DTO 반환
    }

    // 세션 정보 업데이트 API
    @PatchMapping("/session-info")
    public ResponseEntity<String> updateSessionInfo(@RequestBody UserLoginResponseDto requestDto, HttpSession session) {
        UserLoginResponseDto loginUser = (UserLoginResponseDto) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.badRequest().body("로그인 세션이 없습니다.");
        }

        try {
            userService.updateUserContactInfo(loginUser.getLoginid(), requestDto);

            // ✅ 업데이트된 UserEntity 정보를 다시 조회하여 DTO를 재구성
            UserEntity updatedUser = userService.getUserByLoginId(loginUser.getLoginid());
            UserLoginResponseDto updatedDto = new UserLoginResponseDto(
                    updatedUser.getLoginid(),
                    updatedUser.getEmail(), // ✅ 업데이트된 유저에서 이메일 값 할당
                    updatedUser.getNickName(),
                    updatedUser.getPhone(),
                    updatedUser.getBirthday(),
                    updatedUser.getLocation(),
                    updatedUser.getImagePath()
            );
            session.setAttribute("loginUser", updatedDto);

            return ResponseEntity.ok("세션 정보가 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 아이디 찾기 (이메일 존재 여부 검증 후 인증 이메일 발송)
    @GetMapping("/findid/send-auth")
    public ResponseEntity<String> findLoginId(@RequestParam String email, HttpSession session) {
        return userService.findLoginId(email)
                .map(loginId -> {
                    // 인증 코드 생성 및 이메일 발송
                    String authCode = mailService.sendMail(new MailRequestDto(email));

                    // 세션에 인증번호, 이메일, 아이디 저장
                    session.setAttribute("idAuthCode", authCode);
                    session.setAttribute("idAuthEmail", email);
                    session.setAttribute("idAuthLoginId", loginId);
                    session.setMaxInactiveInterval(300); // 세션 5분 만료

                    return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다.");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("해당 이메일로 가입된 계정이 없습니다."));
    }

    // 비밀번호 찾기 (아이디 + 이메일 검증 후 인증번호 발송)
    @PostMapping("/findpw/send-auth")
    public ResponseEntity<String> sendPwAuthCode(@RequestParam String loginid,
                                                 @RequestParam String email,
                                                 HttpSession session) {
        boolean exists = userService.checkUserExists(loginid, email);

        if (!exists) {
            return ResponseEntity.badRequest().body("계정 정보가 일치하지 않습니다.");
        }

        // 인증번호 발송
        String sendpwauth = mailService.sendMail(new MailRequestDto(email));

        // 세션에 인증번호, 로그인 아이디, 이메일 저장
        session.setAttribute("pwAuthCode", sendpwauth);
        session.setAttribute("pwAuthLoginId", loginid);
        session.setAttribute("pwAuthEmail", email);
        session.setMaxInactiveInterval(300); // 세션 5분 만료

        return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다.");
    }

    // 아이디 찾기 (이메일만으로 ID 조회)
    @PostMapping("/findid/verify-id")
    public ResponseEntity<Map<String, String>> findIdByEmail(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "이메일을 입력해주세요."));
        }

        return userService.findLoginId(email)
                .map(loginId -> ResponseEntity.ok(Map.of("loginid", loginId)))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(Map.of("error", "해당 이메일로 가입된 계정이 없습니다.")));
    }

    // 비밀번호 재설정 (이메일 인증번호 검증 후 새 비밀번호 설정)
    @PostMapping("/findpw/verify-pw")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody, HttpSession session) {
        String code = requestBody.get("code");
        String newPassword = requestBody.get("newPassword");

        // 세션에서 저장된 인증 정보 불러오기
        String sessionAuth = (String) session.getAttribute("pwAuthCode");
        String loginId = (String) session.getAttribute("pwAuthLoginId");
        String email = (String) session.getAttribute("pwAuthEmail");

        // 인증번호 확인
        if (sessionAuth == null || !sessionAuth.equals(code)) {
            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
        }

        // 비밀번호 변경
        boolean updated = userService.resetPassword(loginId, email, newPassword);
        if (!updated) {
            return ResponseEntity.badRequest().body("비밀번호 변경 실패");
        }

        // 세션 초기화
        session.invalidate();

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 세션 초기화
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    // 로그인한 사용자 정보 반환 (닉네임 포함) = 필요하다고 해서 메소드 하나 추가해용 ㅠㅠ
    @GetMapping("/me")
    public Map<String, Object> me(org.springframework.security.core.Authentication auth,
                                  HttpSession session) {
        String loginId = null;
        if (auth != null && auth.isAuthenticated()) {
            loginId = auth.getName();
        } else {
            UserLoginResponseDto loginUser = (UserLoginResponseDto) session.getAttribute("loginUser");
            if (loginUser != null) {
                loginId = loginUser.getLoginid();
            }
        }
        if (loginId == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED
            );
        }

        com.example.demo.Login.Entity.UserEntity user = userService.getUserByLoginId(loginId);
        return java.util.Map.of(
                "loginId", user.getLoginid(),
                "nickname", user.getNickName()
        );
    }
}