package com.vehiclemanagement.userservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.userservice.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager,Long>{
	Optional<Manager> findByUser_UserId(Long userId);
    List<Manager> findByIsAvailableTrue();
}
