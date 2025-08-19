package com.subforest.service;

import com.subforest.entity.JwtBlacklist;
import com.subforest.repository.JwtBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final JwtBlacklistRepository blacklistRepository;

    public void addToken(String token, LocalDateTime expiredAt) {
        JwtBlacklist blacklist = JwtBlacklist.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();
        blacklistRepository.save(blacklist);
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepository.findByToken(token).isPresent();
    }

    // ---------------- 새로 추가 ----------------
    public void blacklistToken(String token) {
        addToken(token, LocalDateTime.now().plusHours(1)); // 예: 1시간 후 만료
    }
}
