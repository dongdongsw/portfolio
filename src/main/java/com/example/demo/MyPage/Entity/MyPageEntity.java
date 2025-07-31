package com.example.demo.MyPage.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter //lombok을 통해 간편하게 자동적으로 됨 , 그리고 사용하는 이유는 값을 반환해서 조회하게 할려고 사용
@Setter //lombok을 통해 간편하게 자동적으로 됨 , 그리고 사용하는 이유는 값을 설정하거나 변경하게 할려고 사용
@Table(name = "mypage") // 테이블 이름
@EntityListeners(AuditingEntityListener.class) //@LastModifiedDate를 쓰기 위해서 추가 해야함
public class MyPageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동으로 id 값이 자동으로 증가하게 끔 설정함(그래야 인스턴스들이 고유한 값을 가지는 느낌)
    private int id;

    @Column(name = "login_id", unique = true, length = 25, nullable = false) // unique를 넣어서 중복되지 않게 설정
    private String loginid; //로그인 할 때의 아이디

    @Column(name = "login_pw", length = 25, nullable = false)
    private String loginpw; //로그인 할 때의 비밀번호

    @Column(length = 20, nullable = false)
    private String nickname; //사용자가 설정하는 닉네임

    @Column(length = 35, unique = true, nullable = false)// unique를 넣어서 중복되지 않게 설정
    private String email; //사용자가 비밀번호 찾기나 아이디 찾기 할 떄 사용하는 이메일

    @LastModifiedDate //사용 이유 : 데이터베이스의 해당 컬럼이 수정될 떄 현재 날짜와 시간을 저장하게 하려고
    @Column(name = "modify_date")
    private LocalDateTime modifydate; //최근에 수정한 날짜가 언제인지 알 수 있게하는 컬럼


















}
