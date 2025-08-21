package com.subforest.controller;

import com.subforest.dto.ChangePasswordRequest;
import com.subforest.dto.ChangePasswordResponse;
import com.subforest.dto.NotificationToggleReq;
import com.subforest.entity.User;
import com.subforest.service.UserService;
import com.subforest.service.BlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final UserService userService;
    private final BlacklistService blacklistService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    // 알림 설정 변경
    @PatchMapping("/notification")
    public ResponseEntity<?> updateNotification(
        @AuthenticationPrincipal(expression = "id") Long userId,
        @RequestBody NotificationToggleReq req
    ) {
        boolean enabled = req.getNotificationEnabled();
        boolean result = userService.updateNotification(userId, enabled);
        return ResponseEntity.ok("{\"notificationEnabled\": " + result + "}");
    }

    // 비밀번호 변경
    @PostMapping(value = "/change-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok(new ChangePasswordResponse("Password changed successfully"));
    }

    // 계정 비활성화
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        userService.deactivateAccount(userId);
        return ResponseEntity.ok("{\"message\": \"Account deactivated\"}");
    }

    // 로그아웃 (JWT 블랙리스트 등록)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        blacklistService.blacklistToken(token);
        return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
    }
}
