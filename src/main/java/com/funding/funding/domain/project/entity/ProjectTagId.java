package com.funding.funding.domain.project.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectTagId implements Serializable {
    private Long projectId;
    private Long tagId;

    public ProjectTagId() {}
    public ProjectTagId(Long projectId, Long tagId) {
        this.projectId = projectId; this.tagId = tagId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectTagId that)) return false;
        return Objects.equals(projectId, that.projectId) && Objects.equals(tagId, that.tagId);
    }
    @Override public int hashCode() { return Objects.hash(projectId, tagId); }
}