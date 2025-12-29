package com.vehiclemanagement.userservice.serviceInterface;

import java.util.List;

import com.vehiclemanagement.userservice.entity.Technician;

public interface TechnicianService {
	 Technician assignToManager(Long technicianId, Long managerId);
	 List<Technician> getTechniciansByManager(Long managerId);
}
