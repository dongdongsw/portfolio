/*
package com.example.demo.post.controller;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1. 글 작성
    @PostMapping
    public String createPost(@RequestBody PostRequestDto dto) {
        postService.createPost(dto);
        return "작성 완료";
    }

    // 2. 글 전체 조회
    @GetMapping
    public List<PostResponseDto> getAllPosts() {
        return postService.getAllPosts().stream()
                .map(postService::toResponseDto)
                .collect(Collectors.toList());
    }

    // 3. 글 상세 조회
    @GetMapping("/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 4. 글 수정
    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id, @RequestBody PostRequestDto dto) {
        postService.updatePost(id, dto);
        return "수정 완료";
    }

    // 5. 글 삭제
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "삭제 완료";
    }
}
*/
package com.example.demo.post.controller;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // HttpStatus 임포트
import org.springframework.http.ResponseEntity; // ResponseEntity 임포트
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // 추가

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 1. 글 작성 (파일 업로드 포함)
    @PostMapping
    // 반환 타입을 ResponseEntity<String>으로 변경하고, throws IOException 추가
    public ResponseEntity<String> createPost(
            @ModelAttribute PostRequestDto dto
    ) {
        postService.createPost(dto);
        // HttpStatus.OK (200 OK) 상태 코드와 함께 "작성 완료" 메시지를 반환
        return new ResponseEntity<>("작성 완료", HttpStatus.OK);
    }

    // 나머지 메소드는 기존과 동일

    // 2. 글 전체 조회
    @GetMapping
    public List<PostResponseDto> getAllPosts() {
        return postService.getAllPosts().stream()
                .map(postService::toResponseDto)
                .collect(Collectors.toList());
    }

    // 3. 글 상세 조회
    @GetMapping("/{id}")
    public PostResponseDto getPostById(@PathVariable Long id) {
        return postService.toResponseDto(postService.getPostById(id));
    }

    // 4. 글 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable Long id, @RequestBody PostRequestDto dto) {
        postService.updatePost(id, dto);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK); // 마찬가지로 ResponseEntity 사용
    }

    // 5. 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK); // 마찬가지로 ResponseEntity 사용
    }
}