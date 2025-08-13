package com.example.demo.Comment.Controller;

import com.example.demo.Comment.dto.CommentRequestDto;
import com.example.demo.Comment.dto.CommentResponseDto;
import com.example.demo.Comment.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // JSON 기반 응답, @Controller+@ResponseBody와 같음
@RequestMapping("api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 등록
    @PostMapping("/commentscreate")
    public CommentResponseDto create(@RequestBody CommentRequestDto requestDto) {
        return commentService.createComment(requestDto);
    }


    // 게시글의 전체 댓글 목록 조회
    @GetMapping("/post/{postId}")
    public List<CommentResponseDto> getByPost(@PathVariable int postId) {
        return commentService.getCommentsByPostId(postId);
    }

    // 단일 댓글 조회
    @GetMapping("/singleview/{commentId}")
    public CommentResponseDto getById(@PathVariable int commentId) {
        return commentService.getComment(commentId);
    }

    // 댓글 수정
    @PutMapping("/edit/{commentId}")
    public CommentResponseDto update(@PathVariable int commentId,
                                     @RequestBody CommentRequestDto requestDto) {
        return commentService.updateComment(commentId, requestDto);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public void delete(@PathVariable int commentId) {
        commentService.deleteComment(commentId);
    }
}