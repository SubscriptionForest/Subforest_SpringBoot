package com.subforest.notify;

import com.subforest.entity.Subscription;
import com.subforest.entity.User;

import java.time.LocalDate;

public interface ReminderSender {
    void send(User user, Subscription sub, LocalDate nextBillingDate);
}
