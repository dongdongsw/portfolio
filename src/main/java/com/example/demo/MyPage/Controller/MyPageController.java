package com.example.demo.MyPage.Controller;

import com.example.demo.MyPage.Dto.EmailChangeRequestDto;
import com.example.demo.MyPage.Dto.EmailVerificationRequestDto;
import com.example.demo.MyPage.Dto.PasswordChangeRequestDto;
import com.example.demo.MyPage.Dto.MyPageUpdateRequestDto;
import com.example.demo.MyPage.Dto.UserResponseDto;

import com.example.demo.MyPage.Service.MyPageService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor // final 필드들을 주입받음 (MyPageService, RecentViewService)
public class MyPageController {

    private final MyPageService myPageService;

    // === 유틸리티: 로그인 ID 추출 === (테스트용 메서드 삭제!)
    // 실제 운영 환경에서는 @AuthenticationPrincipal 을 사용해서 로그인 ID를 안전하게 가져옴
    private String getLoginId(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // 여기에 적절한 예외 처리 또는 오류 메시지 로직 추가
            // 예: throw new CustomAuthenticationException("로그인된 사용자 정보를 찾을 수 없습니다.");
            return null; // 또는 null 대신 빈 문자열이나 특정 예외 처리
        }
        return userDetails.getUsername(); // UserDetails 인터페이스에서 사용자 이름을 가져옴 (일반적으로 loginId)
    }

    //  내 정보 조회
    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal UserDetails userDetails) {
        String loginId = getLoginId(userDetails);
        UserResponseDto userInfo = myPageService.getUserInfo(loginId);

        return ResponseEntity.ok(userInfo);
    }

    // 닉네임 중복 확인
    @GetMapping("/nickname/check-availability")
    public ResponseEntity<String> checkNicknameAvailability(
            @RequestParam String nickname) {
        if (myPageService.checkNicknameAvailability(nickname)) {
            return ResponseEntity.ok("사용 가능한 닉네임입니다.");
        } else {
            return ResponseEntity.badRequest().body("이미 사용 중인 닉네임입니다.");
        }
    }

    // 비밀번호 변경 유효성 검증
    @PostMapping("/password/validate")
    public ResponseEntity<String> validatePasswordChange(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequestDto requestDto) {
        String loginId = getLoginId(userDetails);
        String validationError = myPageService.validatePasswordChange(loginId, requestDto);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        return ResponseEntity.ok("비밀번호 변경 조건이 충족됩니다.");
    }

    // 이메일 변경 요청(이메일 인증번호 전송)
    @PostMapping("/email/request-verification")
    public ResponseEntity<String> requestEmailVerification(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EmailChangeRequestDto requestDto) {
        String loginId = getLoginId(userDetails);
        try {
            myPageService.requestEmailChangeVerification(loginId, requestDto);
            return ResponseEntity.ok("새 이메일 주소로 인증번호가 발송되었습니다. 5분 이내에 입력해주세요.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 이메일 인증 코드 검증
    @PostMapping("/email/verify-code")
    public ResponseEntity<String> verifyEmailCode(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EmailVerificationRequestDto requestDto) {
        String loginId = getLoginId(userDetails);
        try {
            if (myPageService.verifyEmailCode(loginId, requestDto.getCode())) {
                return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 최종 마이페이지 업데이트 (수정 완료 버튼 클릭)
    @PatchMapping
    public ResponseEntity<String> updateMyPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MyPageUpdateRequestDto requestDto) {
        String loginId = getLoginId(userDetails);
        try {
            myPageService.updateMyPage(loginId, requestDto);
            return ResponseEntity.ok("회원 정보가 성공적으로 업데이트되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails){
        String loginId = userDetails.getUsername();
        try {
            myPageService.deleteAccount(loginId);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
