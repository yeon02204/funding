package com.funding.funding.domain.project.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "tags")
public class Tag {

    // ✅ getter 추가 — TagService에서 tag.getId() 호출에 필요
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "normalized_name", nullable = false, unique = true, length = 100)
    private String normalizedName;

    public Tag() {}

}