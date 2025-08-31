package com.example.demo.post.service;

import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.entity.PostEntity;
import com.example.demo.post.entity.ImageEntity;
import com.example.demo.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;          // ✅ 추가
import jakarta.persistence.PersistenceContext;   // ✅ 추가
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;                  // ✅ 추가
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/images/";

    @PersistenceContext                      // ✅ 추가: JPQL 업데이트용
    private EntityManager em;

    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    /**
     * ✅ 상세 조회 시 modifydate가 바뀌지 않도록 save() 제거
     *    - 조회수만 JPQL 벌크 업데이트로 +1
     *    - 그 다음 다시 조회해서 반환
     */
    @Transactional
    public PostEntity getPostById(Long id) {
        // 존재 여부 확인
        postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        // 조회수만 +1 (엔티티 더티체크/업데이트 타임스탬프와 무관)
        em.createQuery("update PostEntity p set p.viewcount = p.viewcount + 1 where p.id = :id")
                .setParameter("id", id)
                .executeUpdate();

        // 증가된 값 포함하여 다시 반환
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
    }

    @Transactional
    public PostEntity createPost(PostRequestDto dto) {
        // 파일을 저장할 디렉토리가 없으면 생성합니다.
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        PostEntity post = new PostEntity();
        post.setLoginid(dto.getLoginid());
        post.setNickname(dto.getNickname());
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        post.setViewcount(0);

        ImageEntity imageEntity = new ImageEntity();

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (int i = 0; i < dto.getFiles().size(); i++) {
                MultipartFile file = dto.getFiles().get(i);
                if (!file.isEmpty()) {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String savedFileName = UUID.randomUUID().toString() + extension;
                        File targetFile = new File(directory, savedFileName);

                        file.transferTo(targetFile);
                        String webAccessiblePath = "/images/" + savedFileName;

                        if (i == 0) imageEntity.setImagepath0(webAccessiblePath);
                        else if (i == 1) imageEntity.setImagepath1(webAccessiblePath);
                        else if (i == 2) imageEntity.setImagepath2(webAccessiblePath);
                        else if (i == 3) imageEntity.setImagepath3(webAccessiblePath);
                        else if (i == 4) imageEntity.setImagepath4(webAccessiblePath);

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                    }
                }
            }
        }

        post.setImageEntity(imageEntity);
        return postRepository.save(post);
    }

    @Transactional
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // ✅ 수정할 때만 최종 수정일 갱신
        post.setModifydate(LocalDateTime.now());

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            ImageEntity existingImage = post.getImageEntity();

            ImageEntity image = (existingImage != null) ? existingImage : new ImageEntity();
            if (existingImage == null) {
                post.setImageEntity(image);
            }

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (int i = 0; i < dto.getFiles().size(); i++) {
                MultipartFile file = dto.getFiles().get(i);
                if (!file.isEmpty()) {
                    try {
                        String originalFilename = file.getOriginalFilename();
                        String extension = "";
                        if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                        }
                        String savedFileName = UUID.randomUUID().toString() + extension;
                        File targetFile = new File(directory, savedFileName);

                        file.transferTo(targetFile);

                        String webAccessiblePath = "/images/" + savedFileName;

                        if (i == 0) image.setImagepath0(webAccessiblePath);
                        else if (i == 1) image.setImagepath1(webAccessiblePath);
                        else if (i == 2) image.setImagepath2(webAccessiblePath);
                        else if (i == 3) image.setImagepath3(webAccessiblePath);
                        else if (i == 4) image.setImagepath4(webAccessiblePath);

                    } catch (IOException e) {
                        throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
                    }
                }
            }
        }
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        ImageEntity imageEntity = post.getImageEntity();
        if (imageEntity != null) {
            String[] paths = {
                    imageEntity.getImagepath0(), imageEntity.getImagepath1(), imageEntity.getImagepath2(),
                    imageEntity.getImagepath3(), imageEntity.getImagepath4()
            };
            for (String path : paths) {
                if (path != null && path.startsWith("/images/")) {
                    File fileToDelete = new File(uploadDir, path.substring("/images/".length()));
                    if (fileToDelete.exists()) {
                        fileToDelete.delete();
                    }
                }
            }
        }
        postRepository.delete(post); // 데이터베이스에서 게시글 삭제
    }

    public PostResponseDto toResponseDto(PostEntity post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setLoginid(post.getLoginid());
        dto.setNickname(post.getNickname());
        dto.setContent(post.getContent());
        dto.setModifydate(post.getModifydate());
        dto.setUploaddate(post.getUploaddate());
        dto.setViewcount(post.getViewcount());
        dto.setTitle(post.getTitle());

        ImageEntity image = post.getImageEntity();
        if (image != null) {
            dto.setImagepath0(image.getImagepath0());
            dto.setImagepath1(image.getImagepath1());
            dto.setImagepath2(image.getImagepath2());
            dto.setImagepath3(image.getImagepath3());
            dto.setImagepath4(image.getImagepath4());
        }
        return dto;
    }
}
