package com.subforest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Service: 공통 서비스 마스터 (이름 UNIQUE, 로고 URL).
 * 컨트롤러/서비스 클래스의 @Service와 이름 충돌 주의 → 엔티티는 FQCN 사용 권장.
 */

@Entity
@Table(name = "services",
        uniqueConstraints = @UniqueConstraint(name = "uk_services_name", columnNames = "name"))
@Getter @Setter @NoArgsConstructor
public class Service {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;
}
