package com.subforest.service;

import com.subforest.dto.DashboardSummaryDto;
import com.subforest.entity.Subscription;
import com.subforest.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// 홈 화면에 필요한 요약 데이터(총 월 지출, 구독 개수, 파이/막대 데이터) 생성.
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SubscriptionRepository subscriptionRepository;

    // 간단 집계(MVP): 카테고리는 아직 엔티티에 없으므로 서비스명 기반 샘플 분류
    public DashboardSummaryDto summary(Long userId) {
        List<Subscription> list = subscriptionRepository.findByUserId(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        int totalMonthly = list.stream()
                .mapToInt(s -> monthlyAmount(s.getAmount(), s.getRepeatCycleDays()))
                .sum();

        int activeCount = list.size();

        // 카테고리: 임시 규칙 (필요시 Service 엔티티에 category 추가)
        Map<String, Integer> byCat = new HashMap<>();
        for (Subscription s : list) {
            String name = (s.getService()!=null)? s.getService().getName() : s.getCustomService().getName();
            String cat = guessCategory(name);
            byCat.merge(cat, monthlyAmount(s.getAmount(), s.getRepeatCycleDays()), Integer::sum);
        }

        // 7일간 예정 결제(막대그래프용)
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> upcoming = new TreeMap<>();
        for (int i=0; i<14; i++) { // 2주치
            upcoming.put(today.plusDays(i), 0);
        }
        for (Subscription s : list) {
            LocalDate next = s.getNextBillingDate(today);
            if (!next.isBefore(today) && !next.isAfter(today.plusDays(13))) {
                upcoming.put(next, upcoming.getOrDefault(next, 0) + s.getAmount());
            }
        }
        /**
         * summary(Long userId)
         * totalMonthlySpend: 반복 주기를 월 기준으로 환산(30/90/180/365)
         * activeCount: 구독 개수
         * byCategory: 서비스명 기반 임시 분류(영상/음악/생산성/기타)로 금액 합산 → 원형 차트용
         * upcomingPayments: 오늘~+14일 사이 다음 결제일 날짜별 금액 합산 → 막대 그래프용
         */
        return DashboardSummaryDto.builder()
                .totalMonthlySpend(totalMonthly)
                .activeCount(activeCount)
                .byCategory(byCat.entrySet().stream()
                        .map(e -> new DashboardSummaryDto.PieSlice(e.getKey(), e.getValue()))
                        .collect(Collectors.toList()))
                .upcomingPayments(upcoming.entrySet().stream()
                        .map(e -> new DashboardSummaryDto.DailySum(e.getKey().toString(), e.getValue()))
                        .collect(Collectors.toList()))
                .build();
    }

    private int monthlyAmount(int amount, int cycleDays) {
        // 거친 월환산(MVP): 30/90/180/365 기준
        switch (cycleDays) {
            case 30: return amount;
            case 90: return amount / 3;
            case 180: return amount / 6;
            case 365: return Math.round(amount / 12f);
            default: return amount;
        }
    }

    private String guessCategory(String name) {
        String n = name.toLowerCase();
        if (n.contains("netflix") || n.contains("티빙") || n.contains("웨이브") || n.contains("디즈니")) return "영상";
        if (n.contains("spotify") || n.contains("멜론") || n.contains("지니")) return "음악";
        if (n.contains("ms") || n.contains("office") || n.contains("adobe")) return "생산성";
        return "기타";
    }
}
