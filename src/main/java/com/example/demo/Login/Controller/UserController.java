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


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService; // 💡 여기 추가해야 함!
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

    //아이디 찾기
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

    //아이디 찾기 할때 이메일 인증번호 검증
    @PostMapping("/findid/verify-id")
    public ResponseEntity<String> verifyIdCode(@RequestParam String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("idAuthCode");

        if (savedCode != null && savedCode.equals(code)) {
            String loginId = (String) session.getAttribute("idAuthLoginId");

//            session.removeAttribute("idAuthCode");
//            session.removeAttribute("idAuthEmail");
//            session.removeAttribute("idAuthLoginId");
            //로그인 상태가 아니니 모든 세션 삭제(나중에 필요하다면 개별적인 세션 삭제로 변경 가능)
            session.invalidate();

            return ResponseEntity.ok("회원님의 아이디는: " + loginId); //잘 보내졌으면 서버에서 확인 하는 메시지
        }
        return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다."); //잘 보내졌으면 서버에서 확인 하는 메시지
    }

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


    //비밀번호 재설정
    @PostMapping("/findpw/verify-pw")
    public ResponseEntity<String> resetPassword(@RequestParam String code,
                                                @RequestParam String newPassword,
                                                HttpSession session) {

        String sessionauth = (String) session.getAttribute("pwAuthCode");
        String loginid = (String) session.getAttribute("pwAuthLoginId");
        String email = (String) session.getAttribute("pwAuthEmail");

        if (sessionauth == null || !sessionauth.equals(code)) {//세션에 저장된 인증번호랑 사용자가 입력한 인증번호랑 비교

            return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다."); //잘 보내졌으면 서버에서 확인 하는 메시지

        }

        boolean updated = userService.resetPassword(loginid, email, newPassword);

        if (!updated) {

            return ResponseEntity.badRequest().body("비밀번호 변경 실패"); //잘 보내졌으면 서버에서 확인 하는 메시지

        }

        // 인증 관련 세션 삭제
//        session.removeAttribute("pwAuthCode");
//        session.removeAttribute("pwAuthLoginId");
//        session.removeAttribute("pwAuthEmail");
        //로그인 상태가 아니니 모든 세션 삭제(나중에 필요하다면 개별적인 세션 삭제로 변경 가능)
        session.invalidate();

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다."); //잘 보내졌으면 서버에서 확인 하는 메시지
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {

        session.invalidate(); // 세션 초기화

        return ResponseEntity.ok("로그아웃 되었습니다"); //잘 보내졌으면 서버에서 확인 하는 메시지
    }
}
