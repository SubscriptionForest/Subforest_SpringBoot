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

    // 이름→카테고리 고정 매핑 (요청 목록만 분류, 나머지는 전부 "기타")
    private static final Map<String, String> NAME_TO_CATEGORY = Map.ofEntries(
            // OTT / 영상
            Map.entry("넷플릭스", "영상"),
            Map.entry("웨이브", "영상"),
            Map.entry("티빙", "영상"),
            Map.entry("디즈니플러스", "영상"),
            Map.entry("왓챠", "영상"),
            Map.entry("쿠팡플레이", "영상"),

            // 음악
            Map.entry("스포티파이", "음악"),
            Map.entry("애플뮤직", "음악"),
            Map.entry("멜론", "음악"),
            Map.entry("유튜브뮤직", "음악"),
            Map.entry("지니", "음악"),
            Map.entry("플로", "음악"),
            Map.entry("벅스", "음악"),

            // 멤버십
            Map.entry("네이버 플러스 멤버십", "기타"),
            Map.entry("유튜브 프리미엄", "영상"), // 정책에 따라 조정 가능

            //  전자책
            Map.entry("밀리의서재", "전자책"),

            // 생산성/소프트웨어 (영문 키도 허용)
            Map.entry("notion", "생산성"),
            Map.entry("chatgpt", "생산성"),
            Map.entry("gemini", "생산성"),
            Map.entry("adobe creative cloud", "생산성")
    );

    // 간단 집계(MVP): 카테고리는 아직 엔티티에 없으므로 서비스명 기반 매핑
    public DashboardSummaryDto summary(Long userId) {
        List<Subscription> list = subscriptionRepository
                .findByUserId(userId, org.springframework.data.domain.Pageable.unpaged())
                .getContent();

        int totalMonthly = list.stream()
                .mapToInt(s -> monthlyAmount(s.getAmount(), s.getRepeatCycleDays()))
                .sum();

        int activeCount = list.size();

        // 카테고리 합계
        Map<String, Integer> byCat = new HashMap<>();
        for (Subscription s : list) {
            String name = (s.getService() != null) ? s.getService().getName()
                    : s.getCustomService().getName();
            String cat = guessCategory(name);
            byCat.merge(cat, monthlyAmount(s.getAmount(), s.getRepeatCycleDays()), Integer::sum);
        }

        // 2주치 예정 결제(막대그래프용)
        LocalDate today = LocalDate.now();
        Map<LocalDate, Integer> upcoming = new TreeMap<>();
        for (int i = 0; i < 14; i++) {
            upcoming.put(today.plusDays(i), 0);
        }
        for (Subscription s : list) {
            LocalDate next = s.getNextBillingDate(today);
            if (!next.isBefore(today) && !next.isAfter(today.plusDays(13))) {
                upcoming.put(next, upcoming.getOrDefault(next, 0) + s.getAmount());
            }
        }

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
            case 30:  return amount;
            case 90:  return amount / 3;
            case 180: return amount / 6;
            case 365: return Math.round(amount / 12f);
            default:  return amount;
        }
    }

    // 이름 정규화 후, 고정 매핑에서 포함 순으로 매칭
    private String guessCategory(String name) {
        if (name == null) return "기타";
        String n = normalize(name);

        // 완전 일치 우선
        for (Map.Entry<String, String> e : NAME_TO_CATEGORY.entrySet()) {
            String key = normalize(e.getKey());
            if (n.equals(key)) return e.getValue();
        }
        // 포함 매칭 (예: "디즈니+","애플 뮤직" 등 변형)
        for (Map.Entry<String, String> e : NAME_TO_CATEGORY.entrySet()) {
            String key = normalize(e.getKey());
            if (n.contains(key) || key.contains(n)) return e.getValue();
        }
        return "기타";
    }

    private String normalize(String s) {
        return s.trim().toLowerCase()
                .replace(" ", "")
                .replace("-", "")
                .replace("_", "");
    }
}
