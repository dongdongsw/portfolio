package com.example.demo.MyPage.Dto;

import com.example.demo.Login.Entity.UserEntity;
import lombok.Builder; // 객체를 만들 때 순서에 상관없이 명확하게 만들 수 있게 도와줌
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder // 빌더 패턴을 사용하여 객체를 생성
public class UserResponseDto {
    private String loginid;
    private String nickname;
    private String email;
    private String role;
    private LocalDateTime registdate;
    private LocalDateTime pwupdate;

    // UserEntity 내부 정보가 외부로 노출 방지
    public static UserResponseDto from(UserEntity userEntity) {
        return UserResponseDto.builder()
                .loginid(userEntity.getLoginid())
                .nickname(userEntity.getNickName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .registdate(userEntity.getRegistDate())
                .pwupdate(userEntity.getPwudate())
                .build();
    }
}