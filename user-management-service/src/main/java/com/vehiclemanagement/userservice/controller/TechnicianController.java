package com.vehiclemanagement. userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vehiclemanagement. userservice.dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.dto.response.TechnicianStatsResponse;
import com.vehiclemanagement.userservice.service.TechnicianService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
@Slf4j
public class TechnicianController {
    
    private final TechnicianService technicianService;
    
    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> getAllTechnicians() {
        log.info("GET /api/technicians - Fetch all technicians");
        List<TechnicianResponse> technicians = technicianService.getAllTechnicians();
        return ResponseEntity.ok(technicians);
    }
    
    
    @GetMapping("/unassigned")
    public ResponseEntity<List<TechnicianResponse>> getUnassignedTechnicians() {
        log.info("GET /api/technicians/unassigned - Fetch unassigned technicians");
        List<TechnicianResponse> technicians = technicianService.getUnassignedTechnicians();
        return ResponseEntity. ok(technicians);
    }
    
    @GetMapping("/{technicianId}")
    public ResponseEntity<TechnicianResponse> getTechnicianById(@PathVariable Long technicianId) {
        log.info("GET /api/technicians/{} - Fetch technician by ID", technicianId);
        TechnicianResponse technician = technicianService.getTechnicianById(technicianId);
        return ResponseEntity.ok(technician);
    }
    
    @PutMapping("/{technicianId}/{workassigned}")
    public ResponseEntity<Boolean> assignWork(@PathVariable Long technicianId,@PathVariable Boolean workassigned){
    	Boolean a =technicianService.assignedWork(technicianId,workassigned);
    	return ResponseEntity.ok(a);
    }
    
    @GetMapping("/{technicianId}/assigned-vs-completed")
    public ResponseEntity<TechnicianStatsResponse> getTechnicianStats(@PathVariable Long technicianId) {
        log.info("GET /api/technicians/{}/assigned-vs-completed - Fetch technician stats", technicianId);
        TechnicianStatsResponse stats = technicianService. getTechnicianStats(technicianId);
        return ResponseEntity.ok(stats);
    }
}