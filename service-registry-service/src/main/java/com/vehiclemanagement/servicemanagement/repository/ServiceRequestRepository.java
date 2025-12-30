package com.vehiclemanagement.servicemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vehiclemanagement.servicemanagement.entity.ServiceRequest;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest,Long>{
	 List<ServiceRequest> findByCustomerId(Long customerId);
	    List<ServiceRequest> findByTechnicianId(Long technicianId);
	    List<ServiceRequest> findByStatus(String status);
	    List<ServiceRequest> findByVehicleId(Long vehicleId);
}
