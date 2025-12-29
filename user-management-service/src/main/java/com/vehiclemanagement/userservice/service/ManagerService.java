package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.response.ManagerResponse;
import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.exception.ResourceNotFoundException;
import com.vehiclemanagement.userservice.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework. stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerService {
    
    private final ManagerRepository managerRepository;
    
    public List<ManagerResponse> getAllManagers() {
        log.info("Fetching all managers");
        
        List<Manager> managers = managerRepository.findAll();
        List<ManagerResponse> responseList = new ArrayList<>();
        
        for (Manager manager : managers) {
            ManagerResponse response = mapToResponse(manager);
            responseList.add(response);
        }
        
        return responseList;
    }
    
    public ManagerResponse getManagerById(Long managerId) {
        log.info("Fetching manager by ID: {}", managerId);
        
        Optional<Manager> managerOptional = managerRepository. findById(managerId);
        
        if (!managerOptional.isPresent()) {
            throw new ResourceNotFoundException("Manager not found with ID: " + managerId);
        }
        
        Manager manager = managerOptional.get();
        return mapToResponse(manager);
    }
    
    private ManagerResponse mapToResponse(Manager manager) {
        return ManagerResponse.builder()
                .id(manager.getId())
                .userId(manager.getUserId())
                .name(manager.getName())
                .phone(manager.getPhone())
                .build();
    }
}