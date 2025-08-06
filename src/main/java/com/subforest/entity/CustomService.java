package com.subforest.subscription_manager;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "custom_services")
@Getter
@Setter
@NoArgsConstructor
public class CustomService {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String logoUrl = "/static/images/default_service.png";

    private LocalDateTime createdAt = LocalDateTime.now();
}

