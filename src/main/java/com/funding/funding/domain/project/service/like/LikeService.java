package com.funding.funding.domain.project.service.like;

import com.funding.funding.domain.project.entity.Like;
import com.funding.funding.domain.project.entity.LikeId;
import com.funding.funding.domain.project.entity.Project;
import com.funding.funding.domain.project.repository.LikeRepository;
import com.funding.funding.domain.project.repository.ProjectRepository;
import com.funding.funding.domain.user.entity.User;
import com.funding.funding.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;


     // 좋아요 추가
     // -이미 좋아요한 경우 예외 발생
    @Transactional
    public void like(Long userId, Long projectId) {
        if (likeRepository.existsByIdUserIdAndIdProjectId(userId, projectId)) {
            throw new IllegalStateException("이미 좋아요한 프로젝트입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Like like = new Like();
        setField(like, "id", new LikeId(userId, projectId));
        setField(like, "user", user);
        setField(like, "project", project);

        likeRepository.save(like);
    }


     // 좋아요 취소
     // 좋아요하지 않은 경우 예외 발생
    @Transactional
    public void unlike(Long userId, Long projectId) {
        LikeId id = new LikeId(userId, projectId);
        if (!likeRepository.existsById(id)) {
            throw new IllegalStateException("좋아요하지 않은 프로젝트입니다.");
        }
        likeRepository.deleteById(id);
    }

    // 특정 프로젝트의 좋아요 수
    @Transactional(readOnly = true)
    public long countLikes(Long projectId) {
        return likeRepository.countByIdProjectId(projectId);
    }


    // 내가 이 프로젝트를 좋아요 했는지
    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long projectId) {
        return likeRepository.existsByIdUserIdAndIdProjectId(userId, projectId);
    }

    // Like 엔티티에 public setter가 없으므로 리플렉션으로 필드 세팅
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