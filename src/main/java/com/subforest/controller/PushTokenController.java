package com.subforest.controller;

import com.subforest.dto.PushTokenRequest;
import com.subforest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class PushTokenController {

    //private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody PushTokenRequest req,
                                         Authentication authentication) {
        userService.updateFcmToken(authentication.getName(), req.getFcmToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/toggle")
    public ResponseEntity<Void> toggle(@RequestParam boolean enabled,
                                       Authentication authentication) {
        userService.updatePushEnabled(authentication.getName(), enabled);
        return ResponseEntity.ok().build();
    }


}
