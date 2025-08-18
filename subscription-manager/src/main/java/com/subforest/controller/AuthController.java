package com.subforest.controller;

import com.subforest.config.JwtTokenProvider;
import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
import com.subforest.service.BlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistService blacklistService;

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Valid LoginRequestDto request) {
        User found = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        if (found.getStatus() == UserStatus.DELETED) {
            throw new RuntimeException("탈퇴한 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), found.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        String token = jwtTokenProvider.createToken(found.getEmail());
        return new JwtResponse(token);
    }


    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        blacklistService.addToBlacklist(token);
        return "로그아웃 완료";
    }

    @DeleteMapping("/withdraw")
    public String withdraw(@RequestHeader("Authorization") String authHeader) {
        // 1. 토큰 추출
        String token = authHeader.substring(7);

        // 2. 토큰에서 이메일(subject) 꺼내기
        String email = jwtTokenProvider.getUsername(token);

        // 3. 이메일로 DB에서 사용자 삭제
        userRepository.deleteByEmail(email);

        // 4. 토큰 블랙리스트에 등록 (재사용 방지)
        blacklistService.addToBlacklist(token);

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
