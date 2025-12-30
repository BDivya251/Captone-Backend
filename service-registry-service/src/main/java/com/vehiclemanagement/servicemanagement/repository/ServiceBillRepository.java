package com.vehiclemanagement.servicemanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.servicemanagement.entity.ServiceBill;

public interface ServiceBillRepository extends JpaRepository<ServiceBill,Long>{
	Optional<ServiceBill> findByServiceRequestId(Long serviceRequestId);
}
