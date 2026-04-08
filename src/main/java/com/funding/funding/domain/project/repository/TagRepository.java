package com.funding.funding.domain.project.repository;

import com.funding.funding.domain.project.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // 정규화된 이름으로 태그 조회 (중복 방지용)
    Optional<Tag> findByNormalizedName(String normalizedName);

    // 정규화된 이름 존재 여부
    boolean existsByNormalizedName(String normalizedName);
}