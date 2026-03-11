package com.funding.funding.domain.user.entity;

import com.funding.funding.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String nickname;

    @Column(length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Setter
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "suspended_reason", length = 255)
    private String suspendedReason;

    @Column(name = "deleted_reason", length = 255)
    private String deletedReason;

    protected User() {}

    public User(String email, String nickname, String password, UserRole role, UserStatus status, AuthProvider provider) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.status = status;
        this.provider = provider;
        this.emailVerified = false;
    }

    public static User createSocialUser(
            String email,
            String nickname,
            AuthProvider provider,
            String providerId,
            String profileImage
    ) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.password = null;
        user.role = UserRole.USER;
        user.status = UserStatus.ACTIVE;
        user.provider = provider;
        user.providerId = providerId;
        user.profileImage = profileImage;
        user.emailVerified = true;
        user.emailVerifiedAt = LocalDateTime.now();
        user.lastLoginAt = LocalDateTime.now();
        return user;
    }

    // 이메일 인증 완료
    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
    }

    // 비밀번호 변경
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 프로필 수정
    public void updateProfile(String nickname, String profileImage) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        if (profileImage != null) this.profileImage = profileImage;
    }

    // 소셜 로그인 정보 갱신
    public void updateSocialInfo(String nickname, String profileImage, String providerId) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        if (profileImage != null && !profileImage.isBlank()) this.profileImage = profileImage;
        if (providerId != null && !providerId.isBlank()) this.providerId = providerId;
        this.lastLoginAt = LocalDateTime.now();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // 관리자 회원 정지 / 활성화
    public void suspend(String reason) {
        this.status = UserStatus.SUSPENDED;
        this.suspendedReason = reason;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.suspendedReason = null;
    }
}