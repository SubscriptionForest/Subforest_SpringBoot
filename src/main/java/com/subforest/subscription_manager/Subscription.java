package com.subforest.subscription_manager;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Service service; // nullable

    @ManyToOne(fetch = FetchType.LAZY)
    private CustomService customService; // nullable

    private Integer amount;

    private LocalDate startDate;

    private Integer repeatCycleDays;

    private Boolean autoPayment = false;

    private Boolean isShared = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}

