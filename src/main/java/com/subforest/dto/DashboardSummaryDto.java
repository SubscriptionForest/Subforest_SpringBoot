package com.subforest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class DashboardSummaryDto {
    private Integer totalMonthlySpend;
    private Integer activeCount;
    private List<PieSlice> byCategory;
    private List<DailySum> upcomingPayments;

    @Getter @AllArgsConstructor
    public static class PieSlice { private String category; private Integer amount; }
    @Getter @AllArgsConstructor
    public static class DailySum { private String date; private Integer amount; }
}
