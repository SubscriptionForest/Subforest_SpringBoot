package com.subforest.service;

import com.subforest.dto.*;
import com.subforest.entity.User;
import com.subforest.repository.UserRepository;
//import com.subforest.security.JwtTokenProvider;	// 제거
import com.subforest.security.JwtUtil;			// 추가
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;                    // JwtUtil 주입
    private final BlacklistService blacklistService;

    /** 회원가입 */
    public UserResponse signup(SignupRequestDto request) {
        if (userRepository.findByEmail(request.getEmail().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(User.UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    /** 로그인 */
    public LoginResponse login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new IllegalStateException("탈퇴한 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JwtUtil로 발급
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getName());
    }

    /** 로그아웃 (JWT 블랙리스트 등록) */
    public void logout(String token) {
        // 만료시간은 1시간 뒤로 예시
        blacklistService.addToken(token, java.time.LocalDateTime.now().plusHours(1));
    }

    /** 회원탈퇴 (상태 변경) */
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);
    }
}
