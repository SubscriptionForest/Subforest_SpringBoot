package com.subforest.notify;

import com.subforest.entity.User;
import com.subforest.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final ReminderSender reminderSender;

    // 매일 09:00 (KST) 실행
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void send3DaysBeforeReminders() {
        LocalDate today = LocalDate.now();
        LocalDate target = today.plusDays(3);

        subscriptionRepository.findAll().forEach(sub -> {
            LocalDate next = sub.getNextBillingDate(today);
            if (next != null && next.equals(target)) {
                User user = sub.getUser();
                reminderSender.send(user, sub, next);
            }
        });
    }
}
