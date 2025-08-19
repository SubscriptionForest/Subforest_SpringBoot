package com.subforest.notify.impl;

import com.google.firebase.messaging.*;
import com.subforest.entity.Subscription;
import com.subforest.entity.User;
import com.subforest.notify.ReminderSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class FcmReminderSender implements ReminderSender {

    @Override
    public void send(User user, Subscription sub, LocalDate nextDate) {
        if (user.getFcmToken() == null || user.getFcmToken().isBlank()) {
            log.warn("No FCM token for userId={}", user.getId());
            return;
        }
        if (Boolean.FALSE.equals(user.getPushEnabled())) {
            log.info("Push disabled for userId={}", user.getId());
            return;
        }

        String serviceName = (sub.getService()!=null) ? sub.getService().getName()
                : sub.getCustomService().getName();
        String title = "결제 3일 전 알림";
        String body  = serviceName + " 결제일이 " + nextDate + " 입니다.";

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(user.getFcmToken())
                .setNotification(notification)
                .putData("subscriptionId", String.valueOf(sub.getId()))
                .putData("nextDate", nextDate.toString())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM sent: {}", response);
        } catch (Exception e) {
            log.error("FCM send failed for userId={}, token={}", user.getId(), user.getFcmToken(), e);
        }
    }
}
