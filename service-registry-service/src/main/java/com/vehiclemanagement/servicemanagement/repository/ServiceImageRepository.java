package com.vehiclemanagement.servicemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.servicemanagement.entity.ServiceImage;

public interface ServiceImageRepository extends JpaRepository<ServiceImage,Long>{
	 List<ServiceImage> findByServiceRequestId(Long serviceRequestId);
}
