package com.subforest.repository;

import com.subforest.subscription_manager.CustomService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomServiceRepository extends JpaRepository<CustomService, Long> {
    List<CustomService> findByUserId(Long userId);
}