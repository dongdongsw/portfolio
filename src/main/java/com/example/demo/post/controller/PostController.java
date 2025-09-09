package com.example.demo.post.controller;

import com.example.demo.Login.dto.UserLoginResponseDto;
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final HttpSession httpSession; // 세션에 저장된 loginUser 사용

    // 1) 글 작성
    // (A) 멀티파트: FormData(이미지 포함)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPostMultipart(@ModelAttribute PostRequestDto dto) {
        postService.createPost(dto);
        return "작성 완료";
    }

    // (B) JSON: 이미지 없이 JSON만
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String createPostJson(@RequestBody PostRequestDto dto) {
        postService.createPost(dto);
        return "작성 완료";
    }

    // 2) 글 전체 조회
    @GetMapping
    public List<PostResponseDto> getAllPosts() {
        return postService.getAllPosts().stream()
                .map(postService::toResponseDto)
                .collect(Collectors.toList());
    }

    // 3) 글 상세 조회 (조회수 +1, modifydate 영향 없음)
    @GetMapping("detail/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 4) 글 수정
    // (A) 멀티파트: FormData(이미지 포함)
    @PutMapping(value = "modify/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updatePostMultipart(@PathVariable Long id, @ModelAttribute PostRequestDto dto) {
        postService.updatePost(id, dto);
        return "수정 완료";
    }

    // (B) JSON: 이미지 없이 JSON만
    @PutMapping(value = "modify/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String updatePostJson(@PathVariable Long id, @RequestBody PostRequestDto dto) {
        postService.updatePost(id, dto);
        return "수정 완료";
    }

    // 5) 글 삭제
    @DeleteMapping("delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "삭제 완료";
    }

    // ===============================
    // 6) 프로필 이미지 업로드 / 삭제
    // ===============================

    /** 프로필 이미지 업로드: 세션의 loginUser에서 loginid 가져와 저장 후 User.imagePath 갱신 */
    @PostMapping(
            value = "/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        UserLoginResponseDto loginUser = (UserLoginResponseDto) httpSession.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String loginId = loginUser.getLoginid();

        String webPath = postService.uploadProfileImage(loginId, file); // 예: "/profile/xxxx.png"

        Map<String, String> body = new HashMap<>();
        body.put("imagePath", webPath);
        return ResponseEntity.ok(body);
    }

    /** 프로필 이미지 제거: 내부 파일 삭제 + User.imagePath = null (프론트는 기본 이미지로 표시) */
    @DeleteMapping(value = "/profile-image", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> deleteProfileImage() {
        UserLoginResponseDto loginUser = (UserLoginResponseDto) httpSession.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String loginId = loginUser.getLoginid();

        postService.clearProfileImage(loginId);

        Map<String, String> body = new HashMap<>();
        body.put("imagePath", null);
        return ResponseEntity.ok(body);
    }
}
