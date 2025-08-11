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

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final CustomServiceRepository customServiceRepository;

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

    @Transactional
    public void delete(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        subscriptionRepository.delete(sub);
    }

    @Transactional
    public Page<SubscriptionListItemDto> list(Long userId, Pageable pageable) {
        Page<Subscription> page = subscriptionRepository.findByUser_Id(userId, pageable);
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

    @Transactional
    public Subscription getOneEntity(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
    }
}
