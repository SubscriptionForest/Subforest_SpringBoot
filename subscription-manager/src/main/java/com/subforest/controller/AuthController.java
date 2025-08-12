package com.subforest.controller;

import com.subforest.dto.JwtResponse;
import com.subforest.dto.LoginRequestDto;
import com.subforest.dto.SignupRequestDto;
import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
import com.subforest.security.CustomUserDetails;
import com.subforest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto dto) {
        authService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequestDto dto) {
        String token = authService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    // 예시: 보호된 엔드포인트 - 인증된 사용자 정보 주입
    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(401).body("Unauthorized");
        // principal.getUsername() => email
        return ResponseEntity.ok("Hello, " + principal.getUsername());
    }
}
