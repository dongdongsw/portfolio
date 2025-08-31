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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/images/";

    @PersistenceContext
    private EntityManager em;

    public List<PostEntity> getAllPosts() {
        return postRepository.findAll();
    }

    /** 상세 조회: modifydate 영향 없이 조회수만 +1 */
    @Transactional
    public PostEntity getPostById(Long id) {
        postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        em.createQuery("update PostEntity p set p.viewcount = p.viewcount + 1 where p.id = :id")
                .setParameter("id", id)
                .executeUpdate();

        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
    }

    @Transactional
    public PostEntity createPost(PostRequestDto dto) {
        ensureDirectory();

        PostEntity post = new PostEntity();
        post.setLoginid(dto.getLoginid());
        post.setNickname(dto.getNickname());
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        post.setViewcount(0);

        ImageEntity image = new ImageEntity();

        // 새 파일 저장 (업로드 순서 유지)
        List<String> newPaths = saveFiles(dto.getFiles());

        // 본문에서 첫 번째 이미지 src 추출
        String firstImgSrc = extractFirstImgSrc(dto.getContent());

        // 최종 슬롯 채우기: 처음엔 전부 새 파일뿐이므로,
        // 첫 번째 이미지는 보통 blob → newPaths[0]이 썸네일이 되도록
        List<String> finalOrder = buildFinalOrderForCreate(newPaths, firstImgSrc);

        setImagePaths(image, finalOrder);
        post.setImageEntity(image);

        return postRepository.save(post);
    }

    @Transactional
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        ensureDirectory();

        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setModifydate(LocalDateTime.now());

        ImageEntity image = post.getImageEntity();
        if (image == null) {
            image = new ImageEntity();
            post.setImageEntity(image);
        }

        // 기존 경로들 (순서 유지)
        List<String> existing = getExistingPaths(image);

        // 이번에 추가 업로드한 새 파일 경로
        List<String> newPaths = saveFiles(dto.getFiles());

        // 본문 첫 번째 이미지의 src
        String firstImgSrc = extractFirstImgSrc(dto.getContent());

        // 최종 슬롯 순서 계산 (썸네일=0번 보장)
        List<String> finalOrder = buildFinalOrderForUpdate(existing, newPaths, firstImgSrc);

        setImagePaths(image, finalOrder);
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
        postRepository.delete(post);
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

    // ========================================================================
    // Helper methods
    // ========================================================================

    private void ensureDirectory() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /** 업로드된 파일을 저장하고 웹 경로 리스트를 반환 (최대 5장에 맞춰 쓰는 건 setImagePaths에서 처리) */
    private List<String> saveFiles(List<MultipartFile> files) {
        List<String> paths = new ArrayList<>();
        if (files == null || files.isEmpty()) return paths;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            try {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String savedFileName = UUID.randomUUID().toString() + extension;
                File targetFile = new File(uploadDir, savedFileName);
                file.transferTo(targetFile);

                String webPath = "/images/" + savedFileName;
                paths.add(webPath);
            } catch (IOException e) {
                throw new RuntimeException("이미지 파일 저장에 실패했습니다.", e);
            }
        }
        return paths;
    }

    /** 기존 엔티티의 이미지 경로들을 순서대로 반환 */
    private List<String> getExistingPaths(ImageEntity img) {
        List<String> list = new ArrayList<>();
        if (img.getImagepath0() != null) list.add(img.getImagepath0());
        if (img.getImagepath1() != null) list.add(img.getImagepath1());
        if (img.getImagepath2() != null) list.add(img.getImagepath2());
        if (img.getImagepath3() != null) list.add(img.getImagepath3());
        if (img.getImagepath4() != null) list.add(img.getImagepath4());
        return list;
    }

    /** 첫 번째 <img ... src="..."> 의 src 추출 (없으면 null) */
    private String extractFirstImgSrc(String html) {
        if (html == null || html.isBlank()) return null;
        // src="..." 또는 src='...'
        Pattern p = Pattern.compile("<img\\b[^>]*?src=[\"']([^\"']+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /** 생성(create) 시 최종 순서: 보통 첫 이미지는 blob → newPaths[0]을 0번으로 */
    private List<String> buildFinalOrderForCreate(List<String> newPaths, String firstImgSrc) {
        List<String> out = new ArrayList<>(5);
        if (!newPaths.isEmpty()) {
            // 첫 이미지가 blob이든 아니든, 생성 시에는 새 파일만 존재하므로
            // 에디터에서 가장 먼저 들어온 새 파일이 썸네일 역할을 하도록 newPaths[0]을 0번에 둔다.
            out.add(newPaths.get(0));
            for (int i = 1; i < newPaths.size() && out.size() < 5; i++) {
                out.add(newPaths.get(i));
            }
        }
        return out;
        // (이미지가 하나도 없으면 out 비어 있음 → 썸네일 없음)
    }

    /**
     * 수정(update) 시 최종 순서:
     * - 첫 이미지가 서버 경로(기존 이미지)면 → 그 경로를 0번으로 올리고, 나머지 기존 이미지 순서 + 새 이미지 뒤에 채우기
     * - 첫 이미지가 blob: 이면 → 새로 올린 이미지 중 첫 번째를 0번으로, 나머지(기존→나머지 새) 순서로 채우기
     */
    private List<String> buildFinalOrderForUpdate(List<String> existing, List<String> newPaths, String firstImgSrc) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>(5);

        boolean firstIsExisting = firstImgSrc != null && !firstImgSrc.startsWith("blob:") && existing.contains(firstImgSrc);
        boolean firstIsBlob = firstImgSrc != null && firstImgSrc.startsWith("blob:");

        if (firstIsExisting) {
            // 1) 본문에서 가장 앞에 있는 기존 이미지를 썸네일로
            ordered.add(firstImgSrc);

            // 2) 그 외 기존 이미지들(원 순서) 추가
            for (String ex : existing) {
                if (ordered.size() >= 5) break;
                if (!ordered.contains(ex)) ordered.add(ex);
            }

            // 3) 새로 추가된 이미지들 뒤에 추가
            for (String np : newPaths) {
                if (ordered.size() >= 5) break;
                ordered.add(np);
            }
        } else if (firstIsBlob) {
            // 1) 새 이미지 중 첫 번째를 0번으로
            if (!newPaths.isEmpty()) {
                ordered.add(newPaths.get(0));
            }

            // 2) 기존 이미지들(원 순서)
            for (String ex : existing) {
                if (ordered.size() >= 5) break;
                if (!ordered.contains(ex)) ordered.add(ex);
            }

            // 3) 나머지 새 이미지
            for (int i = 1; i < newPaths.size() && ordered.size() < 5; i++) {
                ordered.add(newPaths.get(i));
            }
        } else {
            // 첫 이미지가 없거나, 본문 첫 이미지가 서버 경로가 아닌 외부 URL 등인 경우:
            // 규칙: 기존 순서 유지 → 그 뒤 새 이미지
            for (String ex : existing) {
                if (ordered.size() >= 5) break;
                ordered.add(ex);
            }
            for (String np : newPaths) {
                if (ordered.size() >= 5) break;
                ordered.add(np);
            }
        }

        // 최대 5개로 자르기
        List<String> result = new ArrayList<>(5);
        for (String s : ordered) {
            result.add(s);
            if (result.size() == 5) break;
        }
        return result;
    }

    /** ImageEntity 슬롯(0~4)에 순서대로 세팅, 초과는 버리고 부족하면 null 유지 */
    private void setImagePaths(ImageEntity img, List<String> ordered) {
        String p0 = ordered.size() > 0 ? ordered.get(0) : null;
        String p1 = ordered.size() > 1 ? ordered.get(1) : null;
        String p2 = ordered.size() > 2 ? ordered.get(2) : null;
        String p3 = ordered.size() > 3 ? ordered.get(3) : null;
        String p4 = ordered.size() > 4 ? ordered.get(4) : null;

        img.setImagepath0(p0);
        img.setImagepath1(p1);
        img.setImagepath2(p2);
        img.setImagepath3(p3);
        img.setImagepath4(p4);
    }

}
