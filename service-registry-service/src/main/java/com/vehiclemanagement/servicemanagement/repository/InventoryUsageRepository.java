package com.vehiclemanagement.servicemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.servicemanagement.entity.InventoryUsage;

public interface InventoryUsageRepository extends JpaRepository<InventoryUsage,Long>{
	List<InventoryUsage> findByServiceRequestId(Long serviceRequestId);
}
