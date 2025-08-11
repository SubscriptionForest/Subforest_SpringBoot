// CustomServiceRepository.java
package com.subforest.repository;

import com.subforest.entity.CustomService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomServiceRepository extends JpaRepository<CustomService, Long> {
    List<CustomService> findByUser_Id(Long userId); // 연관필드(user) 경유
}
