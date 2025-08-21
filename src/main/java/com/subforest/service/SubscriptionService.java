package com.subforest.service;

import com.subforest.dto.*;
import com.subforest.entity.*;
import com.subforest.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
 * SubscriptionService:
 * - create/update/delete/list/listUpcoming/business 검증 로직 담당
 * - list: JPA 기본 목록 + DTO 매핑
 * - listUpcoming: 네이티브 쿼리 결과(Projection) → DTO 매핑 (임박순)
 */

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final CustomServiceRepository customServiceRepository;

    //검증: serviceId XOR customServiceId, repeatCycleDays 허용값, 날짜 파싱
    @Transactional
    public SubscriptionResponseDto create(SubscriptionRequestDto req) {
        if ((req.getServiceId() == null) == (req.getCustomServiceId() == null)) {
            throw new IllegalArgumentException("serviceId 또는 customServiceId 중 하나만 보내세요.");
        }
        int cycle = req.getRepeatCycleDays();
        if (cycle != 30 && cycle != 90 && cycle != 180 && cycle != 365) {
            throw new IllegalArgumentException("반복주기는 30/90/180/365 중 하나여야 합니다.");
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        com.subforest.entity.Service svc = null;
        CustomService custom = null;

        if (req.getServiceId() != null) {
            svc = serviceRepository.findById(req.getServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        } else {
            custom = customServiceRepository.findById(req.getCustomServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("CustomService not found"));
        }

        LocalDate start = LocalDate.parse(req.getStartDate(), DateTimeFormatter.ISO_DATE);

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setService(svc);
        sub.setCustomService(custom);
        sub.setAmount(req.getAmount());
        sub.setStartDate(start);
        sub.setRepeatCycleDays(cycle);
        sub.setAutoPayment(req.getAutoPayment());
        sub.setIsShared(req.getIsShared());

        Subscription saved = subscriptionRepository.save(sub);

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

    //동일 검증 + 서비스 전환 시 한쪽 null 처리
    @Transactional
    public SubscriptionResponseDto update(Long id, SubscriptionRequestDto req) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if ((req.getServiceId() != null) && (req.getCustomServiceId() != null)) {
            throw new IllegalArgumentException("serviceId/customServiceId 중 하나만 선택하세요.");
        }
        int cycle = req.getRepeatCycleDays();
        if (cycle != 30 && cycle != 90 && cycle != 180 && cycle != 365) {
            throw new IllegalArgumentException("반복주기는 30/90/180/365 중 하나여야 합니다.");
        }

        if (req.getUserId() != null) {
            User user = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            sub.setUser(user);
        }

        if (req.getServiceId() != null) {
            com.subforest.entity.Service svc = serviceRepository.findById(req.getServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
            sub.setService(svc);
            sub.setCustomService(null);
        } else if (req.getCustomServiceId() != null) {
            CustomService custom = customServiceRepository.findById(req.getCustomServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("CustomService not found"));
            sub.setCustomService(custom);
            sub.setService(null);
        }

        sub.setAmount(req.getAmount());
        sub.setStartDate(LocalDate.parse(req.getStartDate(), DateTimeFormatter.ISO_DATE));
        sub.setRepeatCycleDays(cycle);
        sub.setAutoPayment(req.getAutoPayment());
        sub.setIsShared(req.getIsShared());

        Subscription saved = subscriptionRepository.save(sub);

        String name = (saved.getService()!=null)? saved.getService().getName(): saved.getCustomService().getName();
        String logo = (saved.getService()!=null)? saved.getService().getLogoUrl(): saved.getCustomService().getLogoUrl();

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

    //존재 확인 후 삭제
    @Transactional
    public void delete(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        subscriptionRepository.delete(sub);
    }

    //기본 목록: JPA 조회 → 엔티티를 SubscriptionListItemDto로 매핑(헬퍼로 nextBillingDate/remainingDays 계산)
    @Transactional
    public Page<SubscriptionListItemDto> list(Long userId, Pageable pageable) {
        Page<Subscription> page = subscriptionRepository.findByUserId(userId, pageable);
        LocalDate today = LocalDate.now();
        return page.map(s -> {
            String name = (s.getService()!=null)? s.getService().getName() : s.getCustomService().getName();
            String logo = (s.getService()!=null)? s.getService().getLogoUrl(): s.getCustomService().getLogoUrl();
            return SubscriptionListItemDto.builder()
                    .id(s.getId())
                    .serviceName(name)
                    .logoUrl(logo)
                    .amount(s.getAmount())
                    .repeatCycleDays(s.getRepeatCycleDays())
                    .nextBillingDate(s.getNextBillingDate(today).toString())
                    .remainingDays(s.getRemainingDays(today))
                    .autoPayment(s.getAutoPayment())
                    .shared(s.getIsShared())
                    .build();
        });
    }

    //단건 상세: fetch join으로 조회 → DTO 반환 (엔티티 직접 반환 금지)
    @Transactional
    public SubscriptionResponseDto getOne(Long id) {
        Subscription s = subscriptionRepository.findByIdWithJoins(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        String name = (s.getService()!=null)? s.getService().getName() :
                      (s.getCustomService()!=null? s.getCustomService().getName() : null);
        String logo = (s.getService()!=null)? s.getService().getLogoUrl() :
                      (s.getCustomService()!=null? s.getCustomService().getLogoUrl() : null);

        return SubscriptionResponseDto.builder()
                .subscriptionId(s.getId())
                .serviceName(name)
                .logoUrl(logo)
                .startDate(s.getStartDate().toString())
                .repeatCycleDays(s.getRepeatCycleDays())
                .autoPayment(s.getAutoPayment())
                .isShared(s.getIsShared())
                .build();
    }
    //임박 순 목록: 네이티브 쿼리 결과(SubscriptionListRow)를 그대로 DTO로 매핑
    @Transactional
    public Page<SubscriptionListItemDto> listUpcoming(Long userId, Pageable pageable) {
        Page<SubscriptionListRow> page = subscriptionRepository.findUpcomingOrder(userId, pageable);
        return page.map(r -> SubscriptionListItemDto.builder()
                .id(r.getId())
                .serviceName(r.getServiceName())
                .logoUrl(r.getLogoUrl())
                .amount(r.getAmount())
                .repeatCycleDays(r.getRepeatCycleDays())
                .nextBillingDate(r.getNextBillingDate().toString())
                .remainingDays(r.getRemainingDays().longValue())
                .autoPayment(r.getAutoPayment())
                .shared(r.getIsShared())
                .build());
    }


}
