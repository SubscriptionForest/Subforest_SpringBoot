package com.subforest.security;

import com.subforest.entity.JwtBlacklist;
import com.subforest.repository.JwtBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

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
}
