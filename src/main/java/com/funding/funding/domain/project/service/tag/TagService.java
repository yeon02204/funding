package com.funding.funding.domain.project.service.tag;

import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.entity.ProjectTag;
import com.funding.funding.domain.project.entity.ProjectTagId;
import com.funding.funding.domain.project.entity.Tag;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.domain.project.repository.ProjectTagRepository;
import com.funding.funding.domain.project.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final ProjectTagRepository projectTagRepository;
    private final ProjectRepository projectRepository;

    /*
      프로젝트에 태그 설정
      - 기존 태그를 전부 지우고 새 태그로 교체
      - 없는 태그명이면 자동 생성
      - 태그명은 소문자 + 공백 제거로 정규화

      @param projectId 프로젝트 ID
      @param tagNames  태그 이름 목록 (ex: ["카페", "디저트", "서울"])
     */
    @Transactional
    public void setTags(Long projectId, List<String> tagNames) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 기존 태그 전부 삭제
        projectTagRepository.deleteByProjectId(projectId);

        // 새 태그 연결
        for (String name : tagNames) {
            String normalized = normalize(name);
            if (normalized.isBlank()) continue;

            // 없으면 새로 만들기
            Tag tag = tagRepository.findByNormalizedName(normalized)
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        setField(t, "name", name.trim());
                        setField(t, "normalizedName", normalized);
                        return tagRepository.save(t);
                    });

            ProjectTag pt = new ProjectTag();
            setField(pt, "id", new ProjectTagId(projectId, tag.getId()));
            setField(pt, "project", project);
            setField(pt, "tag", tag);
            projectTagRepository.save(pt);
        }
    }


    // 프로젝트의 태그 이름 목록 조회
    @Transactional(readOnly = true)
    public List<String> getTagNames(Long projectId) {
        return projectTagRepository.findTagNamesByProjectId(projectId);
    }


    // 태그 정규화 — 소문자 변환 + 앞뒤 공백 제거
    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase();
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}