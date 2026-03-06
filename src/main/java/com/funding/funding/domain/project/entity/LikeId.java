package com.funding.funding.domain.project.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LikeId implements Serializable {
    private Long userId;
    private Long projectId;

    public LikeId() {}
    public LikeId(Long userId, Long projectId) {
        this.userId = userId; this.projectId = projectId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikeId likeId)) return false;
        return Objects.equals(userId, likeId.userId) && Objects.equals(projectId, likeId.projectId);
    }
    @Override public int hashCode() { return Objects.hash(userId, projectId); }
}