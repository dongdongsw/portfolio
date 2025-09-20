package com.example.demo.Comment.Service;

import com.example.demo.Comment.dto.CommentRequestDto;
import com.example.demo.Comment.dto.CommentResponseDto;
import com.example.demo.Comment.Entity.CommentEntity;
import com.example.demo.Comment.Repository.CommentRepository;
import com.example.demo.Login.Entity.UserEntity;
import com.example.demo.Login.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    // 댓글 등록
    @Transactional
    public CommentResponseDto createCommentForPost(int postId, CommentRequestDto requestDto) {
        CommentEntity comment = new CommentEntity();
        comment.setPostId(postId);
        comment.setLoginId(requestDto.getLoginId());
        comment.setContent(requestDto.getContent());
        comment.setUploadDate(java.time.LocalDateTime.now());

        CommentEntity saved = commentRepository.save(comment);
        return new CommentResponseDto(saved, userService.getUserByLoginId(saved.getLoginId()).getNickName());
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(int commentId, CommentRequestDto requestDto) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        comment.setContent(requestDto.getContent());
        CommentEntity updated = commentRepository.save(comment);
        return new CommentResponseDto(updated, userService.getUserByLoginId(updated.getLoginId()).getNickName());
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다");
        }
        commentRepository.deleteById(commentId);
    }

    // 특정 게시글의 전체 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(int postId) {
        List<CommentEntity> comments = commentRepository.findByPostIdOrderByModifyDateDesc(postId);
        return comments.stream().map(entity -> {
            String latestNickname = "Unknown";
            try {
                UserEntity user = userService.getUserByLoginId(entity.getLoginId());
                if(user != null) latestNickname = user.getNickName();
            } catch (Exception ignored) {}
            return new CommentResponseDto(entity, latestNickname);
        }).collect(Collectors.toList());
    }
}