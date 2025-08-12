package com.example.demo.Login.Service;


import com.example.demo.Login.dto.MailRequestDto;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public String sendMail(MailRequestDto dto){
        String authCode = generateRandomNumber(7);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(dto.getToMail());
        message.setSubject("이메일 인증번호 안내");
        message.setText("인증번호: " + authCode + "\n5분 이내에 입력해주세요.");
        message.setFrom(fromEmail);

        javaMailSender.send(message);
        return authCode;
    }

    //random는 현재 시간을 이용한 랜덤으로 하기에 SecureRandom을 사용함 (random은 예측이 가능하고, SecureRandom은 예측이 불가능)
    public String generateRandomNumber(int length){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for(int i = 0; i< length; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
