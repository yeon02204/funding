package com.funding.funding.domain.user.repository;

import com.funding.funding.domain.user.entity.Follow;
import com.funding.funding.domain.user.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    // 팔로우 여부 확인
    boolean existsByIdFollowerIdAndIdFollowingId(Long followerId, Long followingId);

    // 내가 팔로우하는 사람 수
    long countByIdFollowerId(Long followerId);

    // 나를 팔로우하는 사람 수 (팔로워 수)
    long countByIdFollowingId(Long followingId);
}