package com.vehiclemanagement.userservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.entity.Technician;
import com.vehiclemanagement.userservice.repository.ManagerRepository;
import com.vehiclemanagement.userservice.repository.TechnicianRepository;
import com.vehiclemanagement.userservice.serviceInterface.TechnicianService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final ManagerRepository managerRepository;

    @Override
    public Technician assignToManager(Long technicianId, Long managerId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        technician.setManager(manager);
        return technicianRepository.save(technician);
    }

    @Override
    public List<Technician> getTechniciansByManager(Long managerId) {
        return technicianRepository.findByManager_ManagerId(managerId);
    }
}
