package com.subforest.repository;

import com.subforest.entity.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {
    Optional<JwtBlacklist> findByToken(String token);
}
