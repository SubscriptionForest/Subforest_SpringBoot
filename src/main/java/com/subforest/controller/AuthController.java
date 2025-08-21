package com.subforest.controller;

import com.subforest.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.subforest.security.CustomUserDetails;
import com.subforest.dto.JwtResponse;
import com.subforest.dto.LoginRequestDto;
import com.subforest.dto.SignupRequestDto;
import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
// import com.subforest.security.JwtTokenProvider;  // 제거
import com.subforest.security.JwtUtil;               // 추가
import com.subforest.service.BlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;                     // JwtUtil 주입
    private final BlacklistService blacklistService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody @Valid SignupRequestDto request) {
        String email = request.getEmail().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(email)
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(User.UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(saved.getId(), saved.getEmail(), saved.getName()));
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Valid LoginRequestDto request) {
        User found = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        if (found.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("탈퇴한 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), found.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        // JwtUtil 로 발급 (키 경로 단일화)
        String token = jwtUtil.generateToken(found.getEmail());
        return new JwtResponse(token);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 Authorization 헤더");
        }
        String token = authHeader.substring(7).trim();
        blacklistService.addToken(token, LocalDateTime.now().plusHours(1)); // 만료 시간 필요
        return "로그아웃 완료";
    }

    @DeleteMapping("/withdraw")
    public String withdraw(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("유효하지 않은 Authorization 헤더");
        }
        // 1. 토큰 추출
        String token = authHeader.substring(7).trim();

        // 2. JwtUtil로 토큰에서 이메일(subject) 꺼내기
        String email = jwtUtil.getEmailFromToken(token);

        // 3. 이메일로 DB에서 사용자 삭제
        userRepository.deleteByEmail(email);

        // 4. 토큰 블랙리스트에 등록 (재사용 방지)
        blacklistService.addToken(token, LocalDateTime.now().plusHours(1));

        return "회원 탈퇴 완료";
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new UserResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getName()
        );
    }
}
