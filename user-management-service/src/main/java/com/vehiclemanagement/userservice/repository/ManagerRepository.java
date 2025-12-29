package com.vehiclemanagement.userservice.repository;

import com.vehiclemanagement.userservice.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}