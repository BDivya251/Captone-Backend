package com.vehiclemanagement.vehicle.repository;

import com.vehiclemanagement. vehicle.entity.Vehicle;
import org.springframework.data. jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByCustomerId(Long customerId);
    
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    Optional<Vehicle> findByVinNumber(String vinNumber);
    
    boolean existsByVinNumber(String vinNumber);
}