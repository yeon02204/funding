package com.funding.funding.domain.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "project_tags")
public class ProjectTag {

    @EmbeddedId
    private ProjectTagId id;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public ProjectTag() {}
}