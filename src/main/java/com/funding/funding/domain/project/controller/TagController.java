package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 태그 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class TagController {

    private final TagService tagService;

    /*
      프로젝트 태그 설정 (교체)
      PUT /api/projects/{projectId}/tags
      Body: ["카페", "디저트", "서울"]

      기존 태그를 전부 지우고 요청한 태그로 교체한다.
     */
    @PutMapping("/{projectId}/tags")
    public ResponseEntity<Void> setTags(
            @PathVariable Long projectId,
            @RequestBody List<String> tagNames
    ) {
        tagService.setTags(projectId, tagNames);
        return ResponseEntity.noContent().build();
    }

    /*
      프로젝트 태그 목록 조회
      GET /api/projects/{projectId}/tags
     */
    @GetMapping("/{projectId}/tags")
    public ResponseEntity<List<String>> getTags(@PathVariable Long projectId) {
        return ResponseEntity.ok(tagService.getTagNames(projectId));
    }
}