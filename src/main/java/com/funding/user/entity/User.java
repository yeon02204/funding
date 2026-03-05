package com.funding.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role; // USER / ADMIN

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE / SUSPENDED / DELETED

    @Column(nullable = false, length = 20)
    private String provider; // LOCAL / KAKAO / NAVER / GUEST

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(nullable = false)
    private Boolean emailVerified;

    private LocalDateTime emailVerifiedAt;

    @Column(length = 500)
    private String profileImage;

    private LocalDateTime lastLoginAt;

    @Column(length = 255)
    private String suspendedReason;

    @Column(length = 255)
    private String deletedReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}