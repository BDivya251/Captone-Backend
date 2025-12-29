package com.vehiclemanagement. userservice.repository;

import com.vehiclemanagement.userservice.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    
    // Find technicians assigned to a specific manager
    List<Technician> findByManagerId(Long managerId);
    
    // Find technicians without a manager (unassigned)
    List<Technician> findByManagerIsNull();
}