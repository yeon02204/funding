package com.funding.funding.domain.project.repository;

import com.funding.funding.domain.project.entity.Like;
import com.funding.funding.domain.project.entity.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, LikeId> {

    // 특정 프로젝트의 좋아요 수
    long countByIdProjectId(Long projectId);

    // 이미 좋아요 눌렀는지 확인
    boolean existsByIdUserIdAndIdProjectId(Long userId, Long projectId);
}