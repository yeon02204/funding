package com.funding.funding.domain.user.controller;

import com.funding.funding.domain.user.entity.User;
import com.funding.funding.domain.user.service.follow.FollowService;
import com.funding.funding.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> follow(@PathVariable Long userId, Authentication auth) {
        followService.follow(extractUserId(auth), userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId, Authentication auth) {
        followService.unfollow(extractUserId(auth), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> countFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.countFollowing(userId));
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> countFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.countFollowers(userId));
    }

    @GetMapping("/{userId}/follow/me")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, Authentication auth) {
        return ResponseEntity.ok(followService.isFollowing(extractUserId(auth), userId));
    }

    // 팔로워 목록
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Map<String, Object>>> getFollowers(@PathVariable Long userId) {
        List<Map<String, Object>> result = followService.getFollowers(userId)
                .stream()
                .map(u -> Map.<String, Object>of(
                        "id",           u.getId(),
                        "nickname",     u.getNickname(),
                        "profileImage", u.getProfileImage() != null ? u.getProfileImage() : ""
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    // 팔로잉 목록
    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<Map<String, Object>>> getFollowings(@PathVariable Long userId) {
        List<Map<String, Object>> result = followService.getFollowings(userId)
                .stream()
                .map(u -> Map.<String, Object>of(
                        "id",           u.getId(),
                        "nickname",     u.getNickname(),
                        "profileImage", u.getProfileImage() != null ? u.getProfileImage() : ""
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null)
            throw new ApiException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        Object p = auth.getPrincipal();
        if (p instanceof Long id) return id;
        if (p instanceof com.funding.funding.global.security.CustomUserDetails u) return u.getUserId();
        if (p instanceof String s) return Long.valueOf(s);
        throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");
    }
}