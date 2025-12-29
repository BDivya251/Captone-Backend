package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.response.ManagerResponse;
import com.vehiclemanagement. userservice.dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.entity.Manager;
import com.vehiclemanagement.userservice.entity. Technician;
import com.vehiclemanagement.userservice.entity.User;
import com.vehiclemanagement.userservice.enums.UserStatus;
import com.vehiclemanagement.userservice.exception.BadRequestException;
import com.vehiclemanagement.userservice.exception.ResourceNotFoundException;
import com. vehiclemanagement.userservice. repository.ManagerRepository;
import com.vehiclemanagement.userservice.repository.TechnicianRepository;
import com.vehiclemanagement.userservice.repository. UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j. Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerService {
    
    private final ManagerRepository managerRepository;
    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;
    
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
        
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        
        if (!managerOptional.isPresent()) {
            throw new ResourceNotFoundException("Manager not found with ID: " + managerId);
        }
        
        Manager manager = managerOptional. get();
        return mapToResponse(manager);
    }
    
    /**
     * Assign technician to manager and activate both if needed
     * Allows reassignment to different manager
     */
    @Transactional
    public void assignTechnicianToManager(Long managerId, Long technicianId) {
        log.info("Assigning technician ID: {} to manager ID: {}", technicianId, managerId);
        
        // Find Manager
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (!managerOptional.isPresent()) {
            throw new ResourceNotFoundException("Manager not found with ID:  " + managerId);
        }
        Manager manager = managerOptional.get();
        
        // Find Technician
        Optional<Technician> technicianOptional = technicianRepository.findById(technicianId);
        if (!technicianOptional. isPresent()) {
            throw new ResourceNotFoundException("Technician not found with ID: " + technicianId);
        }
        Technician technician = technicianOptional.get();
        
        // Log if technician is being reassigned
        if (technician.getManager() != null) {
            log.warn("Technician ID: {} is being reassigned from manager ID: {} to manager ID: {}", 
                     technicianId, technician.getManager().getId(), managerId);
        }
        
        // Assign technician to manager
        technician.setManager(manager);
        technicianRepository.save(technician);
        
        // Activate Manager Status
        activateManagerStatus(manager. getUserId());
        
        // Activate Technician Status
        activateTechnicianStatus(technician.getUserId());
        
        log.info("Successfully assigned technician ID: {} to manager ID: {}", technicianId, managerId);
    }
    
    /**
     * Activate manager status to ACTIVE
     */
    @Transactional
    public void activateManagerStatus(Long userId) {
        log.info("Activating manager with user ID: {}", userId);
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
        User user = userOptional. get();
        
        if (user.getStatus() == UserStatus.ACTIVE) {
            log.info("Manager user ID: {} is already active", userId);
            return;
        }
        
        user.setStatus(UserStatus. ACTIVE);
        userRepository. save(user);
        
        log.info("Manager user ID:  {} status changed to ACTIVE", userId);
    }
    
    /**
     * Activate technician status to ACTIVE
     */
    @Transactional
    public void activateTechnicianStatus(Long userId) {
        log.info("Activating technician with user ID: {}", userId);
        
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
        User user = userOptional.get();
        
        if (user.getStatus() == UserStatus.ACTIVE) {
            log.info("Technician user ID:  {} is already active", userId);
            return;
        }
        
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        
        log.info("Technician user ID: {} status changed to ACTIVE", userId);
    }
    
    /**
     * Get all technicians under a specific manager
     */
    public List<TechnicianResponse> getTechniciansByManagerId(Long managerId) {
        log.info("Fetching technicians for manager ID: {}", managerId);
        
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (!managerOptional.isPresent()) {
            throw new ResourceNotFoundException("Manager not found with ID: " + managerId);
        }
        
        Manager manager = managerOptional.get();
        List<Technician> technicians = manager.getTechnicians();
        List<TechnicianResponse> responseList = new ArrayList<>();
        
        for (Technician technician : technicians) {
            TechnicianResponse response = TechnicianResponse.builder()
                    .id(technician. getId())
                    .userId(technician.getUserId())
                    .name(technician.getName())
                    .skillSet(technician.getSkillSet())
                    .phone(technician.getPhone())
                    . build();
            responseList.add(response);
        }
        
        log.info("Found {} technicians for manager ID: {}", responseList. size(), managerId);
        return responseList;
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