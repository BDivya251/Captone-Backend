package com.vehiclemanagement.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.repository.ManagerRepository;
import com.vehiclemanagement.userservice.serviceInterface.ManagerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;

    @Override
    public Manager createManager(User user, String department, String specialization) {
        Manager manager = new Manager();
        manager.setUser(user);
        manager.setDepartment(department);
        manager.setSpecialization(specialization);
        manager.setIsAvailable(true);
        return managerRepository.save(manager);
    }

    @Override
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    public Manager getManagerById(Long managerId) {
        return managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
    }

    @Override
    public List<Manager> getAvailableManagers() {
        return managerRepository.findByIsAvailableTrue();
    }
}

