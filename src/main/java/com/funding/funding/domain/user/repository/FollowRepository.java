package com.funding.funding.domain.user.repository;

import com.funding.funding.domain.user.entity.Follow;
import com.funding.funding.domain.user.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    boolean existsByIdFollowerIdAndIdFollowingId(Long followerId, Long followingId);

    long countByIdFollowerId(Long followerId);

    long countByIdFollowingId(Long followingId);

    // 내가 팔로우하는 사람 목록 (팔로잉 목록)
    List<Follow> findByIdFollowerId(Long followerId);

    // 나를 팔로우하는 사람 목록 (팔로워 목록)
    List<Follow> findByIdFollowingId(Long followingId);
}