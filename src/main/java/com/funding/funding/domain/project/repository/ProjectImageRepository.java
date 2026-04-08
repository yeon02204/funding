package com.funding.funding.domain.project.repository;
 
import com.funding.funding.domain.project.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
import java.util.Optional;

/*
 *  Jpareository를 상속
 *
 */
public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
    Optional<ProjectImage> findByProjectIdAndThumbnailTrue(Long projectId);
    List<ProjectImage> findByProjectId(Long projectId);
    void deleteByProjectId(Long projectId);  // 이미지 교체 시 기존 이미지 전체 삭제
    
    void deleteByProjectIdAndImageUrl(Long projectId, String imageUrl);
}