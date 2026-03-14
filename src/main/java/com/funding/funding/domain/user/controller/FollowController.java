package com.funding.funding.domain.user.controller;

import com.funding.funding.domain.user.service.follow.FollowService;
import com.funding.funding.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 팔로우 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;
    private final JwtTokenProvider jwtTokenProvider;

    /*
      팔로우
      POST /api/users/{userId}/follow
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String auth
    ) {
        Long myId = extractUserId(auth);
        followService.follow(myId, userId);
        return ResponseEntity.noContent().build();
    }

    /*
      언팔로우
      DELETE /api/users/{userId}/follow
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollow(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String auth
    ) {
        Long myId = extractUserId(auth);
        followService.unfollow(myId, userId);
        return ResponseEntity.noContent().build();
    }

    /*
      팔로잉 수 (내가 팔로우하는 사람 수)
      GET /api/users/{userId}/following/count
     */
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<Long> countFollowing(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.countFollowing(userId));
    }

    /*
      팔로워 수 (나를 팔로우하는 사람 수)
      GET /api/users/{userId}/followers/count
     */
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<Long> countFollowers(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.countFollowers(userId));
    }

    /*
      내가 이 유저를 팔로우하는지 확인
      GET /api/users/{userId}/follow/me
     */
    @GetMapping("/{userId}/follow/me")
    public ResponseEntity<Boolean> isFollowing(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String auth
    ) {
        Long myId = extractUserId(auth);
        return ResponseEntity.ok(followService.isFollowing(myId, userId));
    }

    private Long extractUserId(String auth) {
        return jwtTokenProvider.getUserId(auth.replace("Bearer ", ""));
    }
}