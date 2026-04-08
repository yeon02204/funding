package com.funding.funding.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
  비밀번호 재설정 토큰 엔티티
  - UUID 기반 토큰을 이메일로 발송
  - 30분 내 사용해야 함
  - 1회 사용 후 usedAt 세팅, 재사용 불가
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public PasswordResetToken() {}

    // ── 팩토리 메서드 ──────────────────────────────────
    public static PasswordResetToken create(User user, String token, int expireMinutes) {
        PasswordResetToken t = new PasswordResetToken();
        t.user      = user;
        t.token     = token;
        t.expiredAt = LocalDateTime.now().plusMinutes(expireMinutes);
        return t;
    }

    // ── 검증 ──────────────────────────────────────────
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed() {
        this.usedAt = LocalDateTime.now();
    }

    // ── Getter ────────────────────────────────────────
    public Long getId()    { return id; }
    public User getUser()  { return user; }
    public String getToken() { return token; }
}