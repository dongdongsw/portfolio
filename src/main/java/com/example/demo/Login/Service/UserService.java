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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    public void signup(UserSignupRequestDto requestDto) {
        // 중복 검사
        if (userRepository.findByLoginid(requestDto.getLoginid()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        //이메일 중복 검사
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserEntity user = new UserEntity();
        user.setLoginid(requestDto.getLoginid());
        user.setUserPwd(passwordEncoder.encode(requestDto.getLoginpw())); // 비밀번호 암호화
        user.setEmail(requestDto.getEmail());
        user.setNickName(requestDto.getNickname());
        user.setRegistDate(LocalDateTime.now());
        user.setRole("USER");

        userRepository.save(user);
    }

    //로그인할 때 아이디와 비밀번호가 맞는지 체크
    public UserLoginResponseDto login(UserLoginRequestDto requestDto){
        //아이디 체크
        UserEntity user = userRepository.findByLoginid(requestDto.getLoginid())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        //비밀번호 체크(평문 비밀번호와 암호화된 비밀번호 비교)
        if(!passwordEncoder.matches(requestDto.getLoginpw(), user.getUserPwd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        //아이디와 비밀번호를 둘다 체크하면 리턴
        return new UserLoginResponseDto(user.getLoginid(), user.getEmail(), user.getNickName());
    }

    //아이디 찾기(이메일로 찾는 기능)
    public Optional<String> findLoginId(String email){

        return userRepository.findByEmail(email)
                .map(UserEntity::getLoginid);
    }

    //비밀번호 찾기에서 로그인 아이디 와 이메일이 두개가 다 맞는 사용자가 있는지 확인
    public boolean checkUserExists(String loginid, String email) {
        return userRepository.findByLoginidAndEmail(loginid, email).isPresent();
    }

    //비밀번호 찾기(찾기 = 비밀번호 재설정과 같다, 로그인 아이디와 이메일로 본인확인 후 새 비밀번호로 재설정)
    public boolean resetPassword(String loginid, String email, String newPassword){
        return userRepository.findByLoginidAndEmail(loginid, email)
                .map(user -> {
                    // 기존 비밀번호와 동일한 경우 예외
                    if (passwordEncoder.matches(newPassword, user.getUserPwd())) {
                        throw new IllegalArgumentException("기존 비밀번호와 다르게 설정해주세요.");
                    }

                    user.setUserPwd(passwordEncoder.encode(newPassword));
                    user.setPwupdate(LocalDateTime.now());
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}
