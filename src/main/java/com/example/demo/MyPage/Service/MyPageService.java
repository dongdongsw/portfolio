package com.example.demo.MyPage.Service;

import com.example.demo.Login.Entity.UserEntity;

import com.example.demo.Login.Repository.UserRepository;

import com.example.demo.Login.Service.MailService;

import com.example.demo.Login.dto.MailRequestDto;
import com.example.demo.MyPage.Dto.EmailChangeRequestDto;
import com.example.demo.MyPage.Dto.PasswordChangeRequestDto;
import com.example.demo.MyPage.Dto.MyPageUpdateRequestDto;
import com.example.demo.MyPage.Dto.UserResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final MailService mailService;

        // 사용자별 이메일 변경 인증 정보를 저장하는 임시 저장소 (Key: loginId)
        private final Map<String, EmailVerificationInfo> emailVerificationStorage = new ConcurrentHashMap<>();

        // 이메일 인증 정보를 담을 내부 클래스
        private static class EmailVerificationInfo {
                private String newEmail;
                private String authCode;
                private LocalDateTime expiryTime;
                private boolean isVerified; // 이메일 인증이 완료되었는지 여부

                public EmailVerificationInfo(String newEmail, String authCode, int expiresInMinutes) {
                        this.newEmail = newEmail;
                        this.authCode = authCode;
                        this.expiryTime = LocalDateTime.now().plusMinutes(expiresInMinutes);
                        this.isVerified = false; // 처음에는 인증되지 않음
                }

                public boolean isExpired() {
                        return LocalDateTime.now().isAfter(expiryTime);
                }

                public String getNewEmail() { return newEmail; }
                public String getAuthCode() { return authCode; }
                public void setVerified(boolean verified) { this.isVerified = verified; }
                public boolean isVerified() { return isVerified; }
        }

        // 내 정보 조회
        public UserResponseDto getUserInfo(String loginId) {
                UserEntity user = userRepository.findByLoginid(loginId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (loginId: " + loginId + ")"));
                return UserResponseDto.from(user);
        }

        // 닉네임 중복 확인
        public boolean checkNicknameAvailability(String newNickname) {
                return userRepository.findByNickname(newNickname).isEmpty(); // 없으면 true (사용 가능)
        }

        // 비밀번호 변경 유효성 검증 (현재 비밀번호와 새 비밀번호 조건만 검증, 실제 DB 변경 안 함)
        public String validatePasswordChange(String loginId, PasswordChangeRequestDto requestDto) {
                UserEntity user = userRepository.findByLoginid(loginId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                // 현재 비밀번호 일치 여부 확인
                if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getUserPwd())) {
                        return "현재 비밀번호가 일치하지 않습니다.";
                }

                // 새 비밀번호와 확인 비밀번호 일치 여부
                if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordConfirm())) {
                        return "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.";
                }

                // 새 비밀번호가 기존 비밀번호와 동일한지 확인
                if (passwordEncoder.matches(requestDto.getNewPassword(), user.getUserPwd())) {
                        return "새 비밀번호는 현재 비밀번호와 달라야 합니다.";
                }

                // 모든 조건 충족 시 null 반환 (오류 없음)
                return null;
        }

        // --- 이메일 변경 요청 (예비 검증: 인증 코드 발송) ---
        @Transactional
        public void requestEmailChangeVerification(String loginId, EmailChangeRequestDto requestDto) {
                String newEmail = requestDto.getNewEmail();

                // 새 이메일 중복 확인 (이미 사용 중인 이메일은 발송 불가)
                if (userRepository.findByEmail(newEmail).isPresent()) {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }

                // 유효한 유저인지 확인 (혹시 모를 상황 대비)
                userRepository.findByLoginid(loginId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                // 인증 코드 생성 및 이메일 발송
                String authCode = mailService.sendMail(new MailRequestDto(newEmail));

                // 인증 정보 저장 (5분 만료)
                // 기존 정보가 있더라도 새로운 요청으로 덮어씀 (이메일 변경 중 다른 이메일로 다시 요청할 수 있으므로)
                emailVerificationStorage.put(loginId, new EmailVerificationInfo(newEmail, authCode, 5));
        }

        // --- 이메일 인증 코드 검증 (예비 검증) ---
        public boolean verifyEmailCode(String loginId, String verificationCode) {
                EmailVerificationInfo storedInfo = emailVerificationStorage.get(loginId);

                if (storedInfo == null || storedInfo.isExpired()) {
                        if (storedInfo != null) emailVerificationStorage.remove(loginId); // 만료된 정보는 삭제
                        throw new IllegalArgumentException("유효한 이메일 변경 요청이 없거나 인증 시간이 만료되었습니다. 다시 시도해주세요.");
                }

                if (!storedInfo.getAuthCode().equals(verificationCode)) {
//                        storedInfo.setVerified(false); // 인증 실패 상태
                        return false;
                }

                // 인증 성공!
                storedInfo.setVerified(true); // 인증 성공 상태로 변경
                // 인증 성공 후 바로 제거하지 않고, 최종 업데이트 시점까지 유지
                return true;
        }


        // 최종 마이페이지 업데이트 (수정 완료 버튼 클릭 시)
        @Transactional // 모든 변경 사항을 한 트랜잭션으로 처리
        public void updateMyPage(String loginId, MyPageUpdateRequestDto requestDto, MultipartFile profileImage) {
                UserEntity user = userRepository.findByLoginid(loginId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                boolean isModified = false; // 실제로 변경된 사항이 있는지 체크

                // 이미지 업로드 처리
                if (profileImage != null && !profileImage.isEmpty()) {
                        try {
                                String uploadDir = "uploads/profile/";
                                Files.createDirectories(Paths.get(uploadDir));

                                String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                                Path filePath = Paths.get(uploadDir, fileName);
                                Files.write(filePath, profileImage.getBytes());

                                user.setImagePath("/profile/" + fileName); // DB에는 '/profile/...' 형식으로 저장
                                isModified = true;
                        } catch (IOException e) {
                                throw new IllegalArgumentException("이미지 업로드 실패: " + e.getMessage());
                        }
                }


                // 닉네임 변경 처리
                if (requestDto.getNewNickname() != null && !requestDto.getNewNickname().isEmpty()) {
                        String newNickname = requestDto.getNewNickname();
                        if (!user.getNickName().equals(newNickname)) { // 현재 닉네임과 다른 경우에만 처리
                                if (userRepository.findByNickname(newNickname).isPresent()) { // 최종 중복 확인 (race condition 방지)
                                        throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
                                }
                                user.setNickName(newNickname);
                                isModified = true;
                        }
                }

                // 비밀번호 변경 처리
                if (requestDto.getCurrentPassword() != null && !requestDto.getCurrentPassword().isEmpty() &&
                        requestDto.getNewPassword() != null && !requestDto.getNewPassword().isEmpty() &&
                        requestDto.getNewPasswordConfirm() != null && !requestDto.getNewPasswordConfirm().isEmpty()) {

                        // 유효성 재검증 (최종 수정 시 한 번 더 검증)
                        PasswordChangeRequestDto pwdRequest = new PasswordChangeRequestDto();
                        pwdRequest.setCurrentPassword(requestDto.getCurrentPassword());
                        pwdRequest.setNewPassword(requestDto.getNewPassword());
                        pwdRequest.setNewPasswordConfirm(requestDto.getNewPasswordConfirm());

                        String validationError = validatePasswordChange(loginId, pwdRequest);
                        if (validationError != null) {
                                throw new IllegalArgumentException(validationError); // 에러 발생 시 처리 중단
                        }

                        user.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
                        isModified = true;
                }

                // 이메일 변경 처리
                if (requestDto.getNewEmail() != null && !requestDto.getNewEmail().isEmpty()) {

                        String requestedNewEmail = requestDto.getNewEmail();
                        EmailVerificationInfo storedInfo = emailVerificationStorage.get(loginId);

                        // isVerified 여부와 이메일 일치 여부만 체크
                        if (storedInfo == null || storedInfo.isExpired() || !storedInfo.isVerified() ||
                                !storedInfo.getNewEmail().equals(requestedNewEmail)) {
                                emailVerificationStorage.remove(loginId);
                                throw new IllegalArgumentException("이메일 인증이 완료되지 않았거나 유효하지 않은 요청입니다. 다시 인증해주세요.");
                        }

                        if (!user.getEmail().equals(requestedNewEmail)) {
                                // 최종 중복 확인 (자기 자신 제외)
                                if (userRepository.findByEmail(requestedNewEmail).isPresent() &&
                                        !userRepository.findByEmail(requestedNewEmail).get().getLoginid().equals(loginId)) {
                                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                                }

                                user.setEmail(requestedNewEmail);
                                isModified = true;
                        }
                        emailVerificationStorage.remove(loginId); // 이메일 변경 완료 후 인증 정보 삭제
                }

                if (requestDto.getPhone() != null) {
                        user.setPhone(requestDto.getPhone());
                        isModified = true;
                }
                if (requestDto.getLocation() != null) {
                        user.setLocation(requestDto.getLocation());
                        isModified = true;
                }
                if (requestDto.getBirthday() != null) {
                        user.setBirthday(requestDto.getBirthday());
                        isModified = true;
                }
                if (requestDto.getImagePath() != null) {
                        user.setImagePath(requestDto.getImagePath());
                        isModified = true;
                }

                // 변경 사항이 있을 경우에만 저장
                if (isModified) {
                        user.setModifyDate(LocalDateTime.now()); //회원 정보 최종 수정일 반영
                        userRepository.save(user); // 한 번에 변경된 UserEntity 저장
                } else {
                        throw new IllegalArgumentException("변경할 내용이 없습니다."); // 변경사항이 없으면 예외를 던지거나, 단순히 성공 메시지 반환
                }
        }

        @Transactional
        public void deleteAccount(String loginId){
                UserEntity user = userRepository.findByLoginid(loginId)
                        .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                userRepository.delete(user);
        }


}