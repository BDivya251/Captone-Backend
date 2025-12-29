package com.vehiclemanagement.userservice.controller;

import com.vehiclemanagement.userservice.dto.response.ManagerResponse;
import com.vehiclemanagement.userservice.dto.response.TechnicianResponse;
import com.vehiclemanagement.userservice.service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ManagerController {
    
    private final ManagerService managerService;
    
    @GetMapping
    public ResponseEntity<List<ManagerResponse>> getAllManagers() {
        log.info("GET /api/managers - Fetch all managers");
        List<ManagerResponse> managers = managerService.getAllManagers();
        return ResponseEntity.ok(managers);
    }
    
    @GetMapping("/{managerId}")
    public ResponseEntity<ManagerResponse> getManagerById(@PathVariable Long managerId) {
        log.info("GET /api/managers/{} - Fetch manager by ID", managerId);
        ManagerResponse manager = managerService.getManagerById(managerId);
        return ResponseEntity.ok(manager);
    }
    
    /**
     * Assign technician to manager
     * POST /api/managers/{managerId}/assign-technician/{technicianId}
     */
    @PostMapping("/{managerId}/assign-technician/{technicianId}")
    public ResponseEntity<Map<String, String>> assignTechnicianToManager(
            @PathVariable Long managerId,
            @PathVariable Long technicianId) {
        
        log.info("POST /api/managers/{}/assign-technician/{}", managerId, technicianId);
        
        managerService.assignTechnicianToManager(managerId, technicianId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Technician successfully assigned to manager");
        response.put("managerId", managerId.toString());
        response.put("technicianId", technicianId.toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all technicians under a specific manager
     * GET /api/managers/{managerId}/technicians
     */
    @GetMapping("/{managerId}/technicians")
    public ResponseEntity<List<TechnicianResponse>> getTechniciansByManager(
            @PathVariable Long managerId) {
        
        log.info("GET /api/managers/{}/technicians", managerId);
        
        List<TechnicianResponse> technicians = managerService. getTechniciansByManagerId(managerId);
        
        return ResponseEntity.ok(technicians);
    }
    
    /**
     * Activate manager status
     * PUT /api/managers/{managerId}/activate
     */
    @PutMapping("/user/{userId}/activate")
    public ResponseEntity<Map<String, String>> activateManager(@PathVariable Long userId) {
        log.info("PUT /api/managers/user/{}/activate", userId);
        
        managerService.activateManagerStatus(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Manager status activated successfully");
        response.put("userId", userId.toString());
        
        return ResponseEntity.ok(response);
    }
}