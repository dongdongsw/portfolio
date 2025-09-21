package com.example.demo.post.controller;

import com.example.demo.Login.Repository.UserRepository;
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    // 생성: POST /api/posts
    @PostMapping
    public ResponseEntity<String> create(@RequestBody PostRequestDto dto) {
        postService.createPost(dto);
        return ResponseEntity.ok("작성 완료");
    }

    // 전체 조회: GET /api/posts
    @GetMapping
    public List<PostResponseDto> list() {
        return postService.getAllPosts().stream()
                .map(postService::toResponseDto)
                .toList();
    }

    // 상세 조회: GET /api/posts/{id}
    @GetMapping("detail/{id}")
    public PostResponseDto detail(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 수정: PUT /api/posts/{id}
    @PutMapping("modify/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody PostRequestDto dto) {
        postService.updatePost(id, dto);
        return ResponseEntity.ok("수정 완료");
    }

    // 삭제: DELETE /api/posts/{id}
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("삭제 완료");
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

    // 최신 글 이미지 경로: GET /api/posts/latest-images
    @GetMapping("/latest-images")
    public List<String> latestImages() {
        return postService.getLatestPostImages();
    }
}
