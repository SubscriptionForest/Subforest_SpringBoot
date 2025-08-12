package com.subforest.dto;

import lombok.Builder;
import lombok.Getter;

/*
 * SubscriptionListItemDto: 목록 표시용 DTO.
 * - 다음 결제일/남은 일수 포함(목록 UI에 바로 반영 가능)
 */

@Getter
@Builder
public class SubscriptionListItemDto {
    private Long id;
    private String serviceName;
    private String logoUrl;
    private Integer amount;
    private Integer repeatCycleDays;
    private String nextBillingDate; // yyyy-MM-dd
    private Long remainingDays;
    private Boolean autoPayment;
    private Boolean shared;
}
