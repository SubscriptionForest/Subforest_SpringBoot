package com.subforest.dto;

import lombok.Builder;
import lombok.Data;

/*
 * SubscriptionResponseDto: 생성/수정 응답용 요약 DTO.
 * - 화면에 필요한 필드만 전달(엔티티 직접 노출 지양)
 */

@Data
@Builder
public class SubscriptionResponseDto {

    private Long subscriptionId;
    private String serviceName;       // 공통 서비스든 커스텀이든 이름만
    private String logoUrl;           // 서비스 로고
    private String startDate;
    private Integer repeatCycleDays;
    private boolean autoPayment;
    private boolean isShared;
}
