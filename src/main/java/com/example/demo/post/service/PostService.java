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

        // 1) 이번 업로드 새 파일 저장
        List<String> newPaths = saveFiles(dto.getFiles());

        // 2) 본문 정리(내부 이미지 dedupe + 최대 5장 제한, blob 매핑)
        SanitizeResult sr = sanitizeHtmlAndCollectInternal(dto.getContent(), Collections.emptyList(), newPaths);

        // 3) 6장 이상이면 거부
        if (sr.internalOrdered.size() > 5) {
            // 업로드했지만 본문에서 쓰지 못한 새 파일은 즉시 정리
            deleteFilesNotInSet(newPaths, new HashSet<>(sr.usedNewPaths));
            throw new RuntimeException("이미지는 최대 5장 삽입 가능합니다.");
        }

        // 4) 사용되지 않은 새 파일 정리(본문에 안 쓰인 blob 매핑 잔여)
        deleteFilesNotInSet(newPaths, new HashSet<>(sr.usedNewPaths));

        // 5) 본문 교체 + 슬롯 반영
        post.setContent(sr.html);
        setImagePaths(image, sr.internalOrdered); // 최대 5장만 들어있음
        post.setImageEntity(image);

        return postRepository.save(post);
    }

    @Transactional
    public PostEntity updatePost(Long id, PostRequestDto dto) {
        ensureDirectory();

        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));

        post.setTitle(dto.getTitle());
        post.setModifydate(LocalDateTime.now());

        ImageEntity image = post.getImageEntity();
        if (image == null) {
            image = new ImageEntity();
            post.setImageEntity(image);
        }

        // 기존 내부 이미지 목록(슬롯 순서)
        List<String> existing = getExistingPaths(image);

        // 이번 업로드 새 파일 저장
        List<String> newPaths = saveFiles(dto.getFiles());

        // 본문 정리
        SanitizeResult sr = sanitizeHtmlAndCollectInternal(dto.getContent(), existing, newPaths);

        // 6장 이상이면 거부
        if (sr.internalOrdered.size() > 5) {
            // 사용되지 않은 새 파일 정리
            deleteFilesNotInSet(newPaths, new HashSet<>(sr.usedNewPaths));
            throw new RuntimeException("이미지는 최대 5장 삽입 가능합니다.");
        }

        // 사용되지 않은 새 파일 정리
        deleteFilesNotInSet(newPaths, new HashSet<>(sr.usedNewPaths));

        // 본문 교체
        post.setContent(sr.html);

        // 슬롯 반영
        setImagePaths(image, sr.internalOrdered);

        // 슬롯에서 빠진 기존 내부 파일은 디스크에서 삭제
        HashSet<String> afterSet = new HashSet<>(sr.internalOrdered);
        for (String old : existing) {
            if (old != null && old.startsWith("/images/") && !afterSet.contains(old)) {
                deleteFileQuietly(old);
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
                    deleteFileQuietly(path);
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

    /** 업로드된 파일을 저장하고 웹 경로 리스트를 반환 */
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

    /** 슬롯(0~4)에 순서대로 세팅 */
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

    /** 파일 삭제 (조용히) */
    private void deleteFileQuietly(String webPath) {
        if (webPath == null || !webPath.startsWith("/images/")) return;
        File f = new File(uploadDir, webPath.substring("/images/".length()));
        if (f.exists()) {
            try { f.delete(); } catch (Exception ignore) {}
        }
    }

    private void deleteFilesNotInSet(List<String> candidates, Set<String> keep) {
        for (String p : candidates) {
            if (p == null) continue;
            if (!keep.contains(p) && p.startsWith("/images/")) {
                deleteFileQuietly(p);
            }
        }
    }

    // ========================================================================
    // HTML sanitize
    //  - blob: → 새 파일 경로로 순서 매핑
    //  - 내부(/images/...) dedupe + 최대 5장만 유지
    //  - 외부(http/https) 이미지는 그대로 둠(내부 5장 제한에는 미포함)
    //  - outline 등 편집 스타일 제거
    //  - 반환: 교체된 html, 최종 내부 이미지 순서, 실제 사용된 새 파일 목록
    // ========================================================================

    private static final Pattern IMG_TAG = Pattern.compile("(?i)<img\\b[^>]*?>");
    private static final Pattern SRC_ATTR = Pattern.compile("(?i)\\bsrc=[\"']([^\"']+)[\"']");
    private static final Pattern STYLE_ATTR = Pattern.compile("(?i)\\bstyle=[\"']([^\"']*)[\"']");

    private SanitizeResult sanitizeHtmlAndCollectInternal(String originalHtml,
                                                          List<String> existing,
                                                          List<String> newPaths) {
        String html = originalHtml == null ? "" : originalHtml;

        // 편집 잔여 outline 제거
        html = removeOutlineFromStyles(html);

        // 결과를 재조립
        StringBuilder out = new StringBuilder(html.length() + 128);

        // 내부 이미지 최종 순서(중복 제거, 등장 순서 보존)
        LinkedHashSet<String> internalOrderedSet = new LinkedHashSet<>(5);

        // blob 매핑용 인덱스 + 실제 사용된 새 파일 기록
        int blobIdx = 0;
        LinkedHashSet<String> usedNew = new LinkedHashSet<>();

        Matcher m = IMG_TAG.matcher(html);
        int last = 0;

        while (m.find()) {
            // 앞쪽 일반 텍스트
            out.append(html, last, m.start());
            last = m.end();

            String imgTag = m.group();

            // src 추출
            Matcher sm = SRC_ATTR.matcher(imgTag);
            if (!sm.find()) {
                // src 없으면 태그 자체 삭제
                continue;
            }
            String src = sm.group(1);

            // 외부 이미지면 그대로 둠 (내부 카운트 X)
            if (isExternal(src)) {
                out.append(imgTag);
                continue;
            }

            // 내부 경로 결정: blob → newPaths 순서대로 매핑, /images/... → 그대로
            String mapped = null;
            if (src.startsWith("blob:")) {
                if (blobIdx < newPaths.size()) {
                    mapped = newPaths.get(blobIdx++);
                    usedNew.add(mapped);
                } else {
                    // 매핑할 새 파일이 없음 → 이 태그는 버림
                    continue;
                }
            } else if (src.startsWith("/images/")) {
                mapped = src;
            } else {
                // 그 외 내부 규칙에 안맞으면 제거
                continue;
            }

            // 내부 이미지는 중복 제거 + 최대 5장까지만 유지
            if (internalOrderedSet.contains(mapped)) {
                // 중복이면 이 태그는 버림
                continue;
            }
            if (internalOrderedSet.size() >= 5) {
                // 6번째 이상이면 버림
                continue;
            }

            internalOrderedSet.add(mapped);

            // 태그 재작성: src=mapped, style에서 outline류 제거
            String rebuilt = rebuildImgTagWithSrcAndCleanStyle(imgTag, mapped);
            out.append(rebuilt);
        }

        // 나머지 텍스트 붙이기
        out.append(html, last, html.length());

        // 결과 셋업
        List<String> internalOrdered = new ArrayList<>(internalOrderedSet);
        return new SanitizeResult(out.toString(), internalOrdered, new ArrayList<>(usedNew));
    }

    private boolean isExternal(String src) {
        if (src == null) return false;
        String s = src.toLowerCase(Locale.ROOT);
        return s.startsWith("http://") || s.startsWith("https://") || s.startsWith("//");
    }

    private String removeOutlineFromStyles(String html) {
        // style="...outline...; outline-...;"
        return STYLE_ATTR.matcher(html).replaceAll(matchResult -> {
            String styles = matchResult.group(1);
            String cleaned = styles
                    .replaceAll("(?i)(^|;)\\s*outline\\s*:[^;\"']*;?", "$1")
                    .replaceAll("(?i)(^|;)\\s*outline-[^:]+:[^;\"']*;?", "$1")
                    .replaceAll(";;+", ";")
                    .replaceAll("^;|;$", "")
                    .trim();
            return cleaned.isEmpty() ? "" : "style=\"" + cleaned + "\"";
        });
    }

    private String rebuildImgTagWithSrcAndCleanStyle(String imgTag, String newSrc) {
        // src 교체(있으면 교체, 없으면 추가 -> 위에서 src 없는 건 이미 걸렀음)
        String replaced = SRC_ATTR.matcher(imgTag).replaceFirst("src=\"" + Matcher.quoteReplacement(newSrc) + "\"");
        // style 안 outline류 제거
        replaced = removeOutlineFromStyles(replaced);
        return replaced;
    }

    // 반환 DTO
    private static class SanitizeResult {
        final String html;
        final List<String> internalOrdered; // 최대 5장(중복 제거)
        final List<String> usedNewPaths;    // 실제 blob 매핑으로 사용된 새 파일
        SanitizeResult(String html, List<String> internalOrdered, List<String> usedNewPaths) {
            this.html = html;
            this.internalOrdered = internalOrdered;
            this.usedNewPaths = usedNewPaths;
        }
    }
}
