package com.example.demo.Login.Repository;

import com.example.demo.Login.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>  {

    //로그인
    Optional<UserEntity> findByLoginid(String loginid);

    //아이디 찾기
    Optional<UserEntity> findByEmail(String email);


    //비밀번호 재설정
    Optional<UserEntity> findByLoginidAndEmail(String loginid, String email);



}
