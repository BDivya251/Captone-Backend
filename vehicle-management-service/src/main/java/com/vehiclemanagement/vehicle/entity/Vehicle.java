package com.vehiclemanagement.vehicle.entity;

import com.vehiclemanagement.vehicle.enums. VehicleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate. annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;
    
    @Column(nullable = false, length = 100)
    private String make; // e.g., Toyota, Honda
    
    @Column(nullable = false, length = 100)
    private String model; // e.g., Camry, Civic
    
    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;
    
    @Column(length = 50)
    private String color;
    
    @Column(name = "vin_number", length = 50, unique = true)
    private String vinNumber; // Vehicle Identification Number
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.ACTIVE;
    
    @Column(length = 500)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}