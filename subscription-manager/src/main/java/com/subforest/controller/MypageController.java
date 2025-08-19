package com.subforest.controller;

import com.subforest.entity.User;
import com.subforest.security.JwtBlacklistService;
import com.subforest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final UserService userService;
    private final JwtBlacklistService jwtBlacklistService;

    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PatchMapping("/notification")
    public ResponseEntity<?> updateNotification(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam boolean enabled
    ) {
        boolean result = userService.updateNotification(userId, enabled);
        return ResponseEntity.ok("{\"notificationEnabled\": " + result + "}");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok("{\"message\": \"Password changed successfully\"}");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        userService.deactivateAccount(userId);
        return ResponseEntity.ok("{\"message\": \"Account deactivated\"}");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        // JWT 토큰 만료 시간 가져오는 로직 (예: JwtTokenProvider 사용)
        LocalDateTime expiry = LocalDateTime.now().plusHours(1); // 예시
        jwtBlacklistService.addToken(token, expiry);
        return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
    }
}
