package com.subforest.controller;

import com.subforest.dto.SubscriptionListItemDto;
import com.subforest.dto.SubscriptionRequestDto;
import com.subforest.dto.SubscriptionResponseDto;
import com.subforest.entity.Subscription;
import com.subforest.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> create(@Valid @RequestBody SubscriptionRequestDto req) {
        return ResponseEntity.ok(subscriptionService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDto> update(@PathVariable Long id,
                                                          @Valid @RequestBody SubscriptionRequestDto req) {
        return ResponseEntity.ok(subscriptionService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<SubscriptionListItemDto>> list(@RequestParam Long userId,
                                                              @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.list(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getOneEntity(id));
    }
}
