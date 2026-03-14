package com.funding.funding.domain.user.service.follow;

import com.funding.funding.domain.user.entity.Follow;
import com.funding.funding.domain.user.entity.FollowId;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.domain.user.repository.FollowRepository;
import com.funding.funding.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    /*
      팔로우
      - 자기 자신 팔로우 불가
      - 이미 팔로우 중이면 예외
     */
    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
        if (followRepository.existsByIdFollowerIdAndIdFollowingId(followerId, followingId)) {
            throw new IllegalStateException("이미 팔로우 중입니다.");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Follow follow = new Follow();
        setField(follow, "id", new FollowId(followerId, followingId));
        setField(follow, "follower", follower);
        setField(follow, "following", following);

        followRepository.save(follow);
    }


    // 언팔로우
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        FollowId id = new FollowId(followerId, followingId);
        if (!followRepository.existsById(id)) {
            throw new IllegalStateException("팔로우하지 않은 사용자입니다.");
        }
        followRepository.deleteById(id);
    }


    // 팔로잉 수 (내가 팔로우하는 사람 수)
    @Transactional(readOnly = true)
    public long countFollowing(Long userId) {
        return followRepository.countByIdFollowerId(userId);
    }


    // 팔로워 수 (나를 팔로우하는 사람 수)
    @Transactional(readOnly = true)
    public long countFollowers(Long userId) {
        return followRepository.countByIdFollowingId(userId);
    }


    // 팔로우 여부 확인
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByIdFollowerIdAndIdFollowingId(followerId, followingId);
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