package com.example.demo.Login.Service;


import com.example.demo.Login.Repository.UserRepository;
import com.example.demo.Login.dto.UserLoginResponseDto;
import com.example.demo.Login.dto.UserLoginRequestDto;
import com.example.demo.Login.Entity.UserEntity;
import com.example.demo.Login.dto.UserSignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public void signup(UserSignupRequestDto requestDto) {
        // 중복 검사
    }



    //로그인할 때 아이디와 비밀번호가 맞는지 체크
    public UserLoginResponseDto login(UserLoginRequestDto requestDto){
        //아이디 체크

    }

    //아이디 찾기(이메일로 찾는 기능)
    public String findLoginId(String email){


    }

    //비밀번호 찾기(찾기 = 비밀번호 재설정과 같다, 로그인 아이디와 이메일로 본인확인 후 새 비밀번호로 재설정)
    public void resetPassword(String loginid, String email, String newPassword){



    }

    public void findIdPw(String loginid, String loginpw, String email){

        //이메일로 인증번호를 보내는 로직

    }
}
