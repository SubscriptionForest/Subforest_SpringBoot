// SubscriptionController.java
package com.subforest.controller;

import com.subforest.dto.SubscriptionRequestDto;
import com.subforest.dto.SubscriptionResponseDto;
import com.subforest.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> createSubscription(
            @Valid @RequestBody SubscriptionRequestDto requestDto) {
        return ResponseEntity.ok(subscriptionService.createSubscription(requestDto));
    }
}
