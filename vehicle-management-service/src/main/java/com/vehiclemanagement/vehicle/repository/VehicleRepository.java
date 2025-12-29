package com.vehiclemanagement.vehicle.repository;

import com.vehiclemanagement. vehicle.entity.Vehicle;
import org.springframework.data. jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    // Find all vehicles by customer ID
    List<Vehicle> findByCustomerId(Long customerId);
    
    // Find vehicle by registration number
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    
    // Check if registration number exists
    boolean existsByRegistrationNumber(String registrationNumber);
    
    // Find vehicle by VIN number
    Optional<Vehicle> findByVinNumber(String vinNumber);
    
    // Check if VIN number exists
    boolean existsByVinNumber(String vinNumber);
}