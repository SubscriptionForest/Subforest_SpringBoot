package com.subforest.dto;

import java.sql.Date;

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
