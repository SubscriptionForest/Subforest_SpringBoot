package com.subforest.repository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByNameContainingIgnoreCase(String name);
}
