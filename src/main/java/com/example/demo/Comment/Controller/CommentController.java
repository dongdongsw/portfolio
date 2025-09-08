package com.example.demo.Comment.Controller;

import com.example.demo.Comment.Dto.CommentRequestDto;
import com.example.demo.Comment.Dto.CommentResponseDto;
import com.example.demo.Comment.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController // JSON 기반 응답, @Controller+@ResponseBody와 같음
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 등록
    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponseDto> create (
            @PathVariable int postId,
            @RequestBody CommentRequestDto requestDto
    ) {
        CommentResponseDto saved = commentService.createCommentForPost(postId, requestDto);
        return ResponseEntity
                .created(URI.create("/api/comments/" + saved.getId()))
                .body(saved);
    }

    // 게시글의 전체 댓글 목록 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDto>> getByPost(@PathVariable int postId) {
        List<CommentResponseDto> list = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(list);
    }

    // 단일 댓글 조회
    @GetMapping("/singleview/{commentId}")
    public ResponseEntity<CommentResponseDto> getById(@PathVariable int commentId) {
        CommentResponseDto dto = commentService.getComment(commentId);
        return ResponseEntity.ok(dto);
    }

    // 댓글 수정
    @PutMapping("/edit/{commentId}")
    public ResponseEntity<CommentResponseDto> update(@PathVariable int commentId,
                                                     @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto updated = commentService.updateComment(commentId, requestDto);
        return ResponseEntity.ok(updated);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable int commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}