package com.subforest.dto;

import java.sql.Date;

//Repository 네이티브 결과를 받기 위한 프로젝션 인터페이스

public interface SubscriptionListRow {
    Long getId();
    String getServiceName();
    String getLogoUrl();
    Integer getAmount();
    Integer getRepeatCycleDays();
    Date getNextBillingDate();
    Integer getRemainingDays();
    Boolean getAutoPayment();
    Boolean getIsShared();
}
