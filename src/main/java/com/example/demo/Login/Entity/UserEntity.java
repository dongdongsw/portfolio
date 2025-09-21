package com.example.demo.Login.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class) //@LastModifiedDate, @CreatedDate를 쓰기 위해서 추가 해야함
public class UserEntity {

    // Getters and Setters


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 회원 고유번호

    @Column(name = "login_id",unique = true, length = 25, nullable = false, updatable = false)
    private String loginid;  // 아이디

    @Column(name = "login_pw", length = 255, nullable = false) //unique는 사용하지 않음 Security를 사용해서 암호화 하기
    private String loginpw;  // 비밀번호

    @Column(unique = true, length = 20, nullable = false)
    private String nickname;  // 닉네임

    @Column (nullable = false, unique = true)
    private String email;  // 이메일

    @Column (nullable = false)
    private String role;  // 권한

    @CreatedDate
    @Column(name = "regist_date", nullable = false)
    private LocalDateTime registdate;  // 회원가입 날짜

    @LastModifiedDate
    private LocalDateTime pwupdate;  // 비밀번호 재설정 날짜

    @Column(length = 15, nullable = true)
    private String phone;  // 전화번호

    @Column(length = 255, nullable = true)
    private String location;  // 주소

    @Column(nullable = true)
    private LocalDate birthday;  // 생일

    @Column(name = "image_path", length = 255, nullable = true)
    private String imagePath;  // 사진 경로

    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;  // 회원 정보 최종 수정 날짜



    public int getId() { return id; }

    public void setId(int id) { this.id = id;}

    public String getLoginid() { return loginid; }

    public void setLoginid(String login_id) { this.loginid = login_id; }

    public String getUserPwd() { return loginpw; }

    public void setUserPwd(String login_pw) { this.loginpw = login_pw; }

    public String getNickName() { return nickname; }

    public void setNickName(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public LocalDateTime getPwudate() { return pwupdate; }

    public void setPwupdate(LocalDateTime pwupdate) { this.pwupdate = pwupdate; }

    public LocalDateTime getRegistDate() { return registdate; }

    public void setRegistDate(LocalDateTime regist_date) { this.registdate = regist_date; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public LocalDate getBirthday() { return birthday; }

    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getImagePath() { return imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    // 비밀번호를 바꾸면 날짜도 자동으로 없데이트 할 수 있게 설정하는 메소드
    public void updatePassword(String encodedPassword) {
        this.loginpw = encodedPassword; // 전달받은 암호화된 비밀번호로 업데이트
        this.pwupdate = LocalDateTime.now(); // 비밀번호 업데이트 시 pwupdate 날짜도 현재 시간으로 업데이트
    }
}