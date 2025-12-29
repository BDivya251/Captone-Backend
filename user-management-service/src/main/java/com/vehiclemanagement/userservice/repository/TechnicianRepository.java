package com.vehiclemanagement.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.userservice.entity.Technician;

public interface TechnicianRepository extends JpaRepository<Technician,Long>{
	List<Technician> findByManager_ManagerId(Long managerId);
    List<Technician> findByIsAvailableTrue();
}
