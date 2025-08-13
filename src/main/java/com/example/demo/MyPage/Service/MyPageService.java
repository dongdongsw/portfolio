package com.example.demo.MyPage.Service;

import com.example.demo.MyPage.Dto.MyPageInfoDto;
import lombok.RequiredArgsConstructor;
import com.example.demo.Login.Repository.UserRepository;
import com.example.demo.Login.

        /*사요자 정보 조회 : 사용자의 로그인 아이디로 db정보를 가져오게끔 설계
          닉네임 변경 기능 : 기능을 새롭게 만든다
          비밀번호 변경 기능 : 로그인쪽에서 사용하는 기능을 그대로 가져가서 사용
          이메일 변경 기능 : 이메일 인증 기능을 새롭게 만들자
         */




@Service
@RequiredArgsConstructor
public class MyPageService {
        private final UserRepository userRepository;

        public void mypageinfo(userRepository dto){

                userRepository.findByEmail(MyPageInfoDto);





        }










}
