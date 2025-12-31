package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.response.AdminResponse;
import com. vehiclemanagement.userservice. entity.Admin;
import com. vehiclemanagement.userservice. entity.User;
import com.vehiclemanagement.userservice.exception.ResourceNotFoundException;
import com.vehiclemanagement.userservice.repository. AdminRepository;
import com.vehiclemanagement.userservice.repository.UserRepository;
import lombok. RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    
    /**
     * Get all admins
     */
    public List<AdminResponse> getAllAdmins() {
        log.info("Fetching all admins");
        
        List<Admin> admins = adminRepository.findAll();
        List<AdminResponse> responseList = new ArrayList<>();
        
        for (Admin admin : admins) {
            Optional<User> user = userRepository.findById(admin.getUserId());
            if (user.isPresent()) {
                AdminResponse response = AdminResponse.builder()
                        . id(admin.getId())
                        .userId(admin.getUserId())
                        .name(admin. getName())
                        .phone(admin.getPhone())
                        .email(user.get().getEmail())
                        .status("ACTIVE")
                        .build();
                responseList.add(response);
            }
        }
        
        return responseList;
    }
    
    /**
     * Get admin by ID
     */
    public AdminResponse getAdminById(Long adminId) {
        log.info("Fetching admin with ID:  {}", adminId);
        
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (!adminOptional.isPresent()) {
            throw new ResourceNotFoundException("Admin not found with ID: " + adminId);
        }
        
        Admin admin = adminOptional.get();
        Optional<User> user = userRepository.findById(admin.getUserId());
        
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User not found for admin");
        }
        
        return AdminResponse.builder()
                .id(admin.getId())
                .userId(admin.getUserId())
                .name(admin.getName())
                .phone(admin. getPhone())
                .email(user.get().getEmail())
                .status("ACTIVE")
                .build();
    }
}