package com.funding.funding.domain.project.controller;

import com.funding.funding.domain.project.service.like.LikeService;
import com.funding.funding.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 좋아요 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider jwtTokenProvider;

    /*
      좋아요
      POST /api/projects/{projectId}/likes
     */
    @PostMapping("/{projectId}/likes")
    public ResponseEntity<Void> like(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String auth
    ) {
        Long userId = extractUserId(auth);
        likeService.like(userId, projectId);
        return ResponseEntity.noContent().build();
    }

    /*
      좋아요 취소
      DELETE /api/projects/{projectId}/likes
     */
    @DeleteMapping("/{projectId}/likes")
    public ResponseEntity<Void> unlike(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String auth
    ) {
        Long userId = extractUserId(auth);
        likeService.unlike(userId, projectId);
        return ResponseEntity.noContent().build();
    }

    /*
      좋아요 수 조회 (비로그인도 가능)
      GET /api/projects/{projectId}/likes/count
     */
    @GetMapping("/{projectId}/likes/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long projectId) {
        return ResponseEntity.ok(likeService.countLikes(projectId));
    }

    /*
      내가 좋아요 했는지 확인
      GET /api/projects/{projectId}/likes/me
     */
    @GetMapping("/{projectId}/likes/me")
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String auth
    ) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(likeService.isLiked(userId, projectId));
    }

    private Long extractUserId(String auth) {
        return jwtTokenProvider.getUserId(auth.replace("Bearer ", ""));
    }
}