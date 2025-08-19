package com.subforest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User: 계정 엔터티
 * - email unique, 비밀번호는 해시 저장(로그인/JWT 파트)
 * - createdAt/updatedAt 수명주기 콜백으로 관리
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "notification_enabled", nullable = false)
    @Builder.Default
    private boolean notificationEnabled = true; // 기본값: ON

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "push_enabled")
    @Builder.Default
    private Boolean pushEnabled = true;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (emailVerified == null) emailVerified = false;
        if (status == null) status = UserStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        DELETED
    }
}
