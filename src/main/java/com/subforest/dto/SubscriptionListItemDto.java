package com.subforest.dto;

import lombok.Builder;
import lombok.Getter;

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
