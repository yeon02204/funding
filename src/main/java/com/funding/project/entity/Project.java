package com.funding.project.entity;

import com.funding.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 (users.id FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 카테고리 (categories.id FK) → 나중에 Category 엔티티 만들 예정
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private Long goalAmount;

    @Column(nullable = false, length = 30)
    private String status;
    // DRAFT / REVIEW / FUNDING / SUCCESS / FAILED / STOPPED / DELETE_REQUESTED / DELETED

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Version
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}