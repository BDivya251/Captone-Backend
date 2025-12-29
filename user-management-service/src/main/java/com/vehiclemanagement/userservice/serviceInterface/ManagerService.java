package com.vehiclemanagement.userservice.serviceInterface;

import java.util.List;

import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.entity.User;

public interface ManagerService {
	  Manager createManager(User user, String department, String specialization);
	    List<Manager> getAllManagers();
	    Manager getManagerById(Long managerId);
	    List<Manager> getAvailableManagers();
}
