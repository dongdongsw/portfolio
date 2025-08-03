package com.example.demo.Login.Controller;

import com.example.demo.Login.dto.UserLoginRequestDto;
import com.example.demo.Login.dto.UserLoginResponseDto;
import com.example.demo.Login.dto.UserSignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Login.Service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService; // 💡 여기 추가해야 함!

    @PostMapping("/create")
    public ResponseEntity<String> signup(@RequestBody UserSignupRequestDto requestDto) {

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto requestDto) {

    }

    // 2. 아이디 찾기
    @GetMapping("/findid")
    public ResponseEntity<String> findLoginId(@RequestParam String email) {

    }

    // 3. 비밀번호 재설정
    @PostMapping("/findpw")
    public ResponseEntity<String> resetPassword(@RequestParam String loginid,
                                                @RequestParam String email,
                                                @RequestParam String newPassword) {

    }


}
