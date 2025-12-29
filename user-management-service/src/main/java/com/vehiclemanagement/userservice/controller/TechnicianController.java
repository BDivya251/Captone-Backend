package com.vehiclemanagement. userservice.controller;

import com.vehiclemanagement.userservice.dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.dto.response.TechnicianStatsResponse;
import com.vehiclemanagement.userservice.service.TechnicianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TechnicianController {
    
    private final TechnicianService technicianService;
    
    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> getAllTechnicians() {
        log.info("GET /api/technicians - Fetch all technicians");
        List<TechnicianResponse> technicians = technicianService.getAllTechnicians();
        return ResponseEntity.ok(technicians);
    }
    
    /**
     * Get unassigned technicians (without a manager)
     * GET /api/technicians/unassigned
     */
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
    
    @GetMapping("/{technicianId}/assigned-vs-completed")
    public ResponseEntity<TechnicianStatsResponse> getTechnicianStats(@PathVariable Long technicianId) {
        log.info("GET /api/technicians/{}/assigned-vs-completed - Fetch technician stats", technicianId);
        TechnicianStatsResponse stats = technicianService. getTechnicianStats(technicianId);
        return ResponseEntity.ok(stats);
    }
}