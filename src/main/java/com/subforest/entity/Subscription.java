package com.subforest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/*
 * Subscription: 구독 엔티티.
 * - user(필수), service/customService(택1, DB상 둘 다 nullable)
 * - startDate + repeatCycleDays로 다음 결제일/남은일수 계산 헬퍼 제공
 * - createdAt은 @PrePersist에서 세팅
 * 주의: service_id와 custom_service_id 매핑 컬럼 혼동 금지
 */

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ERD: user_id BIGINT NOT NULL
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ERD: service_id BIGINT (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;            // nullable

    // ERD: custom_service_id BIGINT (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_service_id")
    private CustomService customService;     // nullable

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "repeat_cycle_days", nullable = false)
    private Integer repeatCycleDays;   // 30/90/180/365

    @Column(name = "auto_payment", nullable = false)
    private Boolean autoPayment = false;

    @Column(name = "is_shared", nullable = false)
    private Boolean isShared = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (autoPayment == null) autoPayment = false;
        if (isShared == null) isShared = false;
    }

    // 다음 결제일/남은일수 헬퍼: 목록/정렬/요약 계산에 유용
    public LocalDate getNextBillingDate(LocalDate today) {
        if (today == null) today = LocalDate.now();
        if (!today.isAfter(startDate)) return startDate;
        long days = ChronoUnit.DAYS.between(startDate, today);
        long cycles = (days / repeatCycleDays) + 1;
        return startDate.plusDays(cycles * repeatCycleDays);
    }

    public long getRemainingDays(LocalDate today) {
        return ChronoUnit.DAYS.between(today != null ? today : LocalDate.now(), getNextBillingDate(today));
    }
}
