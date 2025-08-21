package com.subforest.controller;

import com.subforest.dto.PushTokenRequest;
import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class PushTokenController {

    private final UserRepository userRepository;

    // 토큰 등록: JWT의 subject(email)로 사용자 식별
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody PushTokenRequest req,
                                         Authentication authentication) {
        String email = authentication.getName(); // JWT subject = email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        user.setFcmToken(req.getFcmToken());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    // 알림 on/off 토글: 본인에 대해서만 변경
    @PostMapping("/toggle")
    public ResponseEntity<Void> toggle(@RequestParam("enabled") boolean enabled,
                                       Authentication authentication) {
        String email = authentication.getName(); //  JWT subject = email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        user.setPushEnabled(enabled);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

}
