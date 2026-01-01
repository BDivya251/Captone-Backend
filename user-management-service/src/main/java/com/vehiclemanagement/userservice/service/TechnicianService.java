package com.vehiclemanagement.userservice.service;

import com. vehiclemanagement.userservice. dto.response.TechnicianResponse;
import com.vehiclemanagement. userservice.dto.response.TechnicianStatsResponse;
import com.vehiclemanagement.userservice.entity.Technician;
import com.vehiclemanagement.userservice.exception.ResourceNotFoundException;
import com. vehiclemanagement.userservice. repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework. stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TechnicianService {
    
    private final TechnicianRepository technicianRepository;
    
    public List<TechnicianResponse> getAllTechnicians() {
        log.info("Fetching all technicians");
        
        List<Technician> technicians = technicianRepository.findAll();
        List<TechnicianResponse> responseList = new ArrayList<>();
        
        for (Technician technician : technicians) {
            TechnicianResponse response = mapToResponse(technician);
            responseList.add(response);
        }
        
        return responseList;
    }
    
    public TechnicianResponse getTechnicianById(Long technicianId) {
        log.info("Fetching technician by ID: {}", technicianId);
        
        Optional<Technician> technicianOptional = technicianRepository.findById(technicianId);
        
        if (!technicianOptional.isPresent()) {
            throw new ResourceNotFoundException("Technician not found with ID: " + technicianId);
        }
        
        Technician technician = technicianOptional.get();
        return mapToResponse(technician);
    }
    
    /**
     * Get all unassigned technicians (without a manager)
     */
    public List<TechnicianResponse> getUnassignedTechnicians() {
        log.info("Fetching unassigned technicians");
        
        List<Technician> technicians = technicianRepository.findByManagerIsNull();
        List<TechnicianResponse> responseList = new ArrayList<>();
        
        for (Technician technician :  technicians) {
            TechnicianResponse response = mapToResponse(technician);
            responseList.add(response);
        }
        
        log.info("Found {} unassigned technicians", responseList.size());
        return responseList;
    }
    
    public Boolean assignedWork(Long technicianId,Boolean workassigned) {
    	Technician t=technicianRepository.findById(technicianId).orElseThrow(()-> new RuntimeException("id not found"));
    	
    	t.setWorkAssigned(workassigned);
    	technicianRepository.save(t);
    	return t.getWorkAssigned();
    }
    
    public TechnicianStatsResponse getTechnicianStats(Long technicianId) {
        log.info("Fetching stats for technician ID: {}", technicianId);
        
        // Verify technician exists
        boolean exists = technicianRepository.existsById(technicianId);
        
        if (!exists) {
            throw new ResourceNotFoundException("Technician not found with ID: " + technicianId);
        }
        
        // : This will be implemented when service-management microservice is ready
        // For now, return dummy data
        return TechnicianStatsResponse.builder()
                .technicianId(technicianId)
                .assigned(0)
                .completed(0)
                .inProgress(0)
                .pending(0)
                .build();
    }
    
    private TechnicianResponse mapToResponse(Technician technician) {
        return TechnicianResponse.builder()
                .id(technician.getId())
                .userId(technician.getUserId())
                .name(technician. getName())
                .skillSet(technician.getSkillSet())
                .phone(technician.getPhone())
                .workAssigned(technician.getWorkAssigned())
                .build();
    }
    
}