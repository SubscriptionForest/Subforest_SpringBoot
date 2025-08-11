package com.subforest.dto;

import lombok.Builder;
import lombok.Data;

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
