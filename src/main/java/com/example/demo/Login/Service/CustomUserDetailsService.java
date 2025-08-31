package com.example.demo.Login.Service;


import com.example.demo.Login.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginid) throws UsernameNotFoundException {
        return userRepository.findByLoginid(loginid)
                .map(user -> User.builder()
                        .username(user.getLoginid())                // 아이디
                        .password(user.getUserPwd())                // 암호화된 비밀번호
                        .roles("USER")                              // 권한 (필요에 따라 DB 컬럼 기반으로 변경 가능)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다: " + loginid));
    }
}
