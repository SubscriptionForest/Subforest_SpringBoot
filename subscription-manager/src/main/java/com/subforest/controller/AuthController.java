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
    public String signup(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User found = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        if (!passwordEncoder.matches(user.getPassword(), found.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        return jwtTokenProvider.createToken(found.getUsername());
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        blacklistService.addToBlacklist(token);
        return "로그아웃 완료";
    }

    @DeleteMapping("/withdraw")
    public String withdraw(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtTokenProvider.getUsername(token);

        userRepository.deleteByUsername(username);
        blacklistService.addToBlacklist(token);

        return "회원 탈퇴 완료";
    }

    // 로그인된 사용자 정보 가져오기
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new UserResponse(userDetails.getId(), userDetails.getEmail(), userDetails.getUsername());
    }

}
