package com.vehiclemanagement.userservice.controller;

import com.vehiclemanagement.userservice.dto.response.AdminResponse;
import com. vehiclemanagement.userservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

import java.util.List;

/**
 * Admin Controller - Manage admin profiles
 */
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final AdminService adminService;
    
   
    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        log.info("GET /api/admins - Fetch all admins");
        List<AdminResponse> admins = adminService.getAllAdmins();
        return ResponseEntity. ok(admins);
    }
    
   
    @GetMapping("/{adminId}")
    public ResponseEntity<AdminResponse> getAdminById(@PathVariable Long adminId) {
        log.info("GET /api/admins/{} - Fetch admin by ID", adminId);
        AdminResponse admin = adminService.getAdminById(adminId);
        return ResponseEntity.ok(admin);
    }
}