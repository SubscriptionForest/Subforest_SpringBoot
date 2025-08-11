package com.subforest.repository;

import com.subforest.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByName(String name);
    List<Service> findTop10ByNameContainingIgnoreCaseOrderByNameAsc(String q); // LIKE 검색용
}
