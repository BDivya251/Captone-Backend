package com.vehiclemanagement.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.userservice.entity.RegistrationRequest;
import com.vehiclemanagement.userservice.entity.RequestStatus;
import com.vehiclemanagement.userservice.entity.Role;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest,Long>{
	 List<RegistrationRequest> findByStatus(RequestStatus status);
	    List<RegistrationRequest> findByRoleAndStatus(Role role, RequestStatus status);
}
