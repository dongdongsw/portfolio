package com.example.demo.post.controller;

import com.example.demo.Login.dto.UserLoginResponseDto;
import com.example.demo.Login.Repository.UserRepository; // ← 패키지 경로 그대로
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;       // 생성자 주입
    private final UserRepository userRepository; // 생성자 주입

    // ★ 명시적 생성자 주입으로 초기화 보장 (Lombok 필요 없음)
    @Autowired
    public PostController(PostService postService,
                          UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    // 1) 글 작성
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createPostMultipart(@ModelAttribute PostRequestDto dto) {
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

    // 3) 글 상세 조회
    @GetMapping("detail/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 4) 글 수정
    @PutMapping(value = "modify/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updatePostMultipart(@PathVariable Long id, @ModelAttribute PostRequestDto dto) {
        postService.updatePost(id, dto);
        return "수정 완료";
    }

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

    // 작성자 공개 정보 조회 (loginid 기준): GET /api/posts/author?loginid=xxx
    @GetMapping("/author")
    public ResponseEntity<Map<String, Object>> author(@RequestParam String loginid) {
        return userRepository.findByLoginid(loginid)
                .map(u -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("loginId",   u.getLoginid());
                    body.put("nickName",  u.getNickName());
                    body.put("email",     u.getEmail());
                    body.put("phone",     u.getPhone());
                    body.put("birthday",  u.getBirthday());
                    body.put("imagePath", u.getImagePath());
                    body.put("location",  u.getLocation());
                    return ResponseEntity.ok(body);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //
    @GetMapping("/latest-images")
    public List<String> getLatestPostImages() {
        return postService.getLatestPostImages();
    }

}