package com.vehiclemanagement.userservice.controller;

import com.vehiclemanagement.userservice.dto.response. ManagerResponse;
import com. vehiclemanagement.userservice. service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

import java.util.List;

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
}