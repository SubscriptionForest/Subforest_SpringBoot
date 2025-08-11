package com.subforest.service;

import com.subforest.dto.SubscriptionRequestDto;
import com.subforest.dto.SubscriptionResponseDto;
import com.subforest.entity.*;
import com.subforest.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final CustomServiceRepository customServiceRepository;

    @Transactional
    public SubscriptionResponseDto createSubscription(SubscriptionRequestDto req) {
        // 단일 선택(공통 vs 커스텀)
        if ((req.getServiceId() == null) == (req.getCustomServiceId() == null)) {
            throw new IllegalArgumentException("serviceId 또는 customServiceId 중 하나만 보내세요.");
        }

        // 반복주기 허용값 체크
        int cycle = req.getRepeatCycleDays();
        if (cycle != 30 && cycle != 90 && cycle != 180 && cycle != 365) {
            throw new IllegalArgumentException("반복주기는 30/90/180/365 중 하나여야 합니다.");
        }

        // 사용자
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 서비스/커스텀
        //Service svc = null;
        com.subforest.entity.Service svc = null;
        com.subforest.entity.CustomService custom = null;
        //CustomService custom = null;
        if (req.getServiceId() != null) {
            svc = serviceRepository.findById(req.getServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        } else {
            custom = customServiceRepository.findById(req.getCustomServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("CustomService not found"));
        }

        // 날짜 파싱
        LocalDate start = LocalDate.parse(req.getStartDate(), DateTimeFormatter.ISO_DATE);

        // 엔티티 생성
        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setService(svc);
        sub.setCustomService(custom);
        sub.setAmount(req.getAmount());
        sub.setStartDate(start);
        sub.setRepeatCycleDays(cycle);
        sub.setAutoPayment(req.isAutoPayment());
        sub.setIsShared(req.isShared());

        Subscription saved = subscriptionRepository.save(sub);

        // 이름/로고 매핑
        String name = (svc != null) ? svc.getName() : custom.getName();
        String logo = (svc != null) ? svc.getLogoUrl() : custom.getLogoUrl();

        return SubscriptionResponseDto.builder()
                .subscriptionId(saved.getId())
                .serviceName(name)
                .logoUrl(logo)
                .startDate(saved.getStartDate().toString())
                .repeatCycleDays(saved.getRepeatCycleDays())
                .autoPayment(saved.getAutoPayment())
                .isShared(saved.getIsShared())
                .build();
    }
}
