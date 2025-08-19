package com.subforest.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jwt_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500, unique = true)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt; // 토큰 만료 시각 저장
}
