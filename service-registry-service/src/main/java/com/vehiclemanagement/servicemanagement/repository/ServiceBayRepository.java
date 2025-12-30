package com.vehiclemanagement.servicemanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vehiclemanagement.servicemanagement.entity.ServiceBay;

@Repository
public interface ServiceBayRepository extends JpaRepository<ServiceBay,Long>{
	Optional<ServiceBay> findByBayNumber(String bayNumber);
	List<ServiceBay> findByIsAvailableAndIsActive(Boolean isAvailable,Boolean isActive);
	List<ServiceBay> findByIsActive(Boolean isActive);
	boolean existsByBayNumber(String bayNumber);
}
