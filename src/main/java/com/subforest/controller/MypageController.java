package com.subforest.controller;

import com.subforest.dto.ChangePasswordRequest;
import com.subforest.entity.User;
import com.subforest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final UserService userService;
    //private final JwtBlacklistService jwtBlacklistService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@AuthenticationPrincipal(expression = "id") Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    // 알림 설정 변경
    @PatchMapping("/notification")
    public ResponseEntity<?> updateNotification(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestParam boolean enabled
    ) {
        boolean result = userService.updateNotification(userId, enabled);
        //return ResponseEntity.ok("{\"notificationEnabled\": " + result + "}");
        return ResponseEntity.ok(Map.of("notificationEnabled", result));
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ChangePasswordRequest req  // ← 이렇게
    ) {
        userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    // 계정 비활성화
    @PostMapping("/deactivate")
    public ResponseEntity<Map<String, String>> deactivateAccount(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        userService.deactivateAccount(userId);
        //return ResponseEntity.ok("{\"message\": \"Account deactivated\"}");
        return ResponseEntity.ok(Map.of("message", "Account deactivated"));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        //jwtBlacklistService.blacklistToken(token);
        //return ResponseEntity.ok("{\"message\": \"Logged out successfully\"}");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
