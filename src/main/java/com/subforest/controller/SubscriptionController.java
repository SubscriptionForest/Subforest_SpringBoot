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

/*
 * SubscriptionController:
 * - REST 엔드포인트 집합. 파라미터/응답 포맷 담당.
 * - 비즈니스 처리는 서비스에 위임하여 얇게 유지.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    //create 요청 DTO @valid
    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> create(@Valid @RequestBody SubscriptionRequestDto req) {
        return ResponseEntity.ok(subscriptionService.create(req));
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDto> update(@PathVariable("id") Long id,
                                                          @Valid @RequestBody SubscriptionRequestDto req) {
        return ResponseEntity.ok(subscriptionService.update(id, req));
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //기본 목록(페이지네이션/정렬)
    @GetMapping
    public ResponseEntity<Page<SubscriptionListItemDto>> list(@RequestParam("userId") Long userId,
                                                              @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.list(userId, pageable));
    }

    //단건 상세: 엔티티 대신 DTO로 반환 (Lazy Proxy 직렬화 오류 방지)
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponseDto> getOne(@PathVariable("id") Long id) {
        return ResponseEntity.ok(subscriptionService.getOne(id));
    }

    //결제 임박 순 목록
    @GetMapping("/upcoming")
    public ResponseEntity<Page<SubscriptionListItemDto>> listUpcoming(
            @RequestParam("userId") Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(subscriptionService.listUpcoming(userId, pageable));
    }

}
