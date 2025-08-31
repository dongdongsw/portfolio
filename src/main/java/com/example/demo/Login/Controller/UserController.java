package com.example.demo.Login.Controller;

import com.example.demo.Login.Service.MailService;
import com.example.demo.Login.dto.MailRequestDto;
import com.example.demo.Login.dto.UserLoginRequestDto;
import com.example.demo.Login.dto.UserLoginResponseDto;
import com.example.demo.Login.dto.UserSignupRequestDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Login.Service.UserService;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final MailService mailService;


    //회원가입
    @PostMapping("/create")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto); //UserService 파일의 signup 클래스를 실행

        return ResponseEntity.ok("회원가입이 완료되었습니다."); //잘 보내졌으면 서버에서 확인 하는 메시지
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto requestDto, HttpSession session) {
        UserLoginResponseDto responseDto = userService.login(requestDto);

        session.setAttribute("loginUser", responseDto); // 세션에  loginid, loginpw, nickname 저장

        session.setMaxInactiveInterval(1800); // 30분 세션만료

        return ResponseEntity.ok("로그인이 완료되었습니다"); //잘 보내졌으면 서버에서 확인 하는 메시지
    }

    //아이디 찾기(아이디존재를 검증 후, 인증 이메일 보내기)
    @GetMapping("/findid/send-auth")
    public ResponseEntity<String> findLoginId(@RequestParam String email,  HttpSession session) {
        return userService.findLoginId(email)
                .map(loginId -> {
                    String authCode = mailService.sendMail(new MailRequestDto(email));
                    //@AllArgsConstructor 을 MailRequestDto에서 사용해서 인자를 1개 를 넣을 수 있게 함

                    session.setAttribute("idAuthCode", authCode); //세션에 인증번호 저장
                    session.setAttribute("idAuthEmail", email); //세션에 이메일 저장
                    session.setAttribute("idAuthLoginId", loginId); // 세션에 아이디 저장

                    session.setMaxInactiveInterval(300); //세션만료 5분

                    return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다."); //잘 보내졌으면 서버에서 확인 하는 메시지
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("해당 이메일로 가입된 계정이 없습니다.")); //이메일이 데이터베이스에 없으면 서버에서 확인하는 메시지

    }



    //비밀번호 찾기(아이디와 이메일을 묶어서 검증 후, 인증번호 이메일 보내기)
    @PostMapping("/findpw/send-auth")
    public ResponseEntity<String> sendPwAuthCode(@RequestParam String loginid,
                                                 @RequestParam String email,
                                                 HttpSession session) {

        boolean exists = userService.checkUserExists(loginid, email);

        if (!exists) {
            return ResponseEntity.badRequest().body("계정 정보가 일치하지 않습니다.");
        }


        String sendpwauth = mailService.sendMail(new MailRequestDto(email));

        session.setAttribute("pwAuthCode", sendpwauth);
        session.setAttribute("pwAuthLoginId", loginid);
        session.setAttribute("pwAuthEmail", email);

        session.setMaxInactiveInterval(300); //세션만료 5분


        return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다."); //잘 보내졌으면 서버에서 확인 하는 메시지
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



    //비밀번호 찾기(이메일 인증번호 검증 후, 비밀번호 재설정)
    @PostMapping("/findpw/verify-pw")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody, HttpSession session) {
        String code = requestBody.get("code");
        String newPassword = requestBody.get("newPassword");

        // 사용자의 세션 정보를 불러옴
        String sessionAuth = (String) session.getAttribute("pwAuthCode");
        String loginId = (String) session.getAttribute("pwAuthLoginId");
        String email = (String) session.getAttribute("pwAuthEmail");

        // 세션에 저장된 인증번호와 사용자가 입력한 인증번호 비교
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


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {

        session.invalidate(); // 세션 초기화

        return ResponseEntity.ok("로그아웃 되었습니다"); //잘 보내졌으면 서버에서 확인 하는 메시지
    }
}
