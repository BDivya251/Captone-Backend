package com.vehiclemanagement.servicemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_bays")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bay_number", nullable = false, unique = true, length = 20)
    private String bayNumber; // BAY-01, BAY-02, etc. 
    
    @Column(name = "bay_name", length = 100)
    private String bayName; // "Main Service Bay 1"
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true; // true = available, false = in use
    
    @Column(name = "current_service_request_id")
    private Long currentServiceRequestId; // Track which service is using this bay
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // For maintenance/deactivation
}