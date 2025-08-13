package com.example.demo.MyPage.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/*사요자 정보 조회 : 사용자의 로그인 아이디로 db정보를 가져오게끔 설계
          닉네임 변경 기능 : 기능을 새롭게 만든다
          비밀번호 변경 기능 : 로그인쪽에서 사용하는 기능을 그대로 가져가서 사용
          이메일 변경 기능 : 이메일 인증 기능을 새롭게 만들자
         */


@Controller
@RequestMapping("/api/mypage")
public class MyPageController {


    //사용자 정보 조회
    @GetMapping("/info/{loginid}")
    public MypageUserInfo{

    }

    //닉네임 중복 체크
    @GetMapping("/check/nickname")
    public MypageCheckNickname{

    }

    //이메일 인증 번호 전송
    @PostMapping()

    //사용자 정보 전체 수정 완료 처리(닉네임, 이메일, 비밀번호)
    @PatchMapping("/edit/total")
    public MypageeEditInfo{

    }







}
