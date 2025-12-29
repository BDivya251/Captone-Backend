package com.vehiclemanagement.vehicle.service;

import com.vehiclemanagement.vehicle.client.UserServiceClient;
import com.vehiclemanagement.vehicle.dto.feign.CustomerResponse;
import com.vehiclemanagement.vehicle.dto.request.CreateVehicleRequest;
import com.vehiclemanagement.vehicle.dto.request.UpdateVehicleRequest;
import com. vehiclemanagement.vehicle.dto.response.VehicleResponse;
import com.vehiclemanagement.vehicle.entity.Vehicle;
import com.vehiclemanagement.vehicle. exception.BadRequestException;
import com.vehiclemanagement.vehicle. exception.FeignClientException;
import com. vehiclemanagement.vehicle.exception.ResourceNotFoundException;
import com.vehiclemanagement.vehicle.repository.VehicleRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util. ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {
    
    private final VehicleRepository vehicleRepository;
    private final UserServiceClient userServiceClient;  // ✅ Inject Feign Client
    
    /**
     * Validate if customer exists by calling User Service
     */
    private void validateCustomerExists(Long customerId) {
        log.info("Validating customer ID: {} with User Service", customerId);
        
        try {
            CustomerResponse customer = userServiceClient.getCustomerById(customerId);
            log.info("Customer validation successful: Customer ID {} exists with name {}", 
                     customerId, customer.getName());
        } catch (FeignException. NotFound ex) {
            log.error("Customer not found:  {}", customerId);
            throw new BadRequestException("Customer with ID " + customerId + " does not exist");
        } catch (FeignException ex) {
            log.error("Error communicating with User Service: {}", ex.getMessage());
            throw new FeignClientException("Unable to validate customer.  User Service may be unavailable.", ex);
        }
    }
    
    /**
     * Create a new vehicle
     */
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        log.info("Creating vehicle for customer ID: {}", request.getCustomerId());
        
        // ✅ STEP 1: Validate customer exists via User Service
        validateCustomerExists(request.getCustomerId());
        
        // STEP 2: Check if registration number already exists
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BadRequestException("Vehicle with registration number " + 
                                         request.getRegistrationNumber() + " already exists");
        }
        
        // STEP 3: Check if VIN number already exists (if provided)
        if (request.getVinNumber() != null && !request.getVinNumber().trim().isEmpty()) {
            if (vehicleRepository.existsByVinNumber(request.getVinNumber())) {
                throw new BadRequestException("Vehicle with VIN number " + 
                                             request.getVinNumber() + " already exists");
            }
        }
        
        // STEP 4: Create vehicle entity
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomerId(request.getCustomerId());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request. getModel());
        vehicle.setYearOfManufacture(request.getYearOfManufacture());
        vehicle.setColor(request.getColor());
        vehicle.setVinNumber(request.getVinNumber());
        vehicle.setNotes(request.getNotes());
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        log.info("Vehicle created successfully with ID: {}", savedVehicle.getId());
        
        return mapToResponse(savedVehicle);
    }
    
    /**
     * Get all vehicles
     */
    public List<VehicleResponse> getAllVehicles() {
        log.info("Fetching all vehicles");
        
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleResponse> responseList = new ArrayList<>();
        
        for (Vehicle vehicle : vehicles) {
            VehicleResponse response = mapToResponse(vehicle);
            responseList.add(response);
        }
        
        log.info("Found {} vehicles", responseList.size());
        return responseList;
    }
    
    /**
     * Get vehicle by ID
     */
    public VehicleResponse getVehicleById(Long vehicleId) {
        log.info("Fetching vehicle by ID: {}", vehicleId);
        
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        
        if (! vehicleOptional.isPresent()) {
            throw new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        
        Vehicle vehicle = vehicleOptional.get();
        return mapToResponse(vehicle);
    }
    
    /**
     * Get all vehicles by customer ID
     */
    public List<VehicleResponse> getVehiclesByCustomerId(Long customerId) {
        log.info("Fetching vehicles for customer ID: {}", customerId);
        
        // ✅ Validate customer exists
        validateCustomerExists(customerId);
        
        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(customerId);
        List<VehicleResponse> responseList = new ArrayList<>();
        
        for (Vehicle vehicle : vehicles) {
            VehicleResponse response = mapToResponse(vehicle);
            responseList.add(response);
        }
        
        log.info("Found {} vehicles for customer ID: {}", responseList.size(), customerId);
        return responseList;
    }
    
    /**
     * Update vehicle
     */
    @Transactional
    public VehicleResponse updateVehicle(Long vehicleId, UpdateVehicleRequest request) {
        log.info("Updating vehicle ID: {}", vehicleId);
        
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        
        if (!vehicleOptional.isPresent()) {
            throw new ResourceNotFoundException("Vehicle not found with ID:  " + vehicleId);
        }
        
        Vehicle vehicle = vehicleOptional.get();
        
        // Update registration number if provided and different
        if (request.getRegistrationNumber() != null && 
            !request.getRegistrationNumber().equals(vehicle.getRegistrationNumber())) {
            
            if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
                throw new BadRequestException("Vehicle with registration number " + 
                                             request.getRegistrationNumber() + " already exists");
            }
            vehicle.setRegistrationNumber(request. getRegistrationNumber());
        }
        
        // Update VIN number if provided and different
        if (request.getVinNumber() != null && 
            !request.getVinNumber().equals(vehicle.getVinNumber())) {
            
            if (vehicleRepository.existsByVinNumber(request.getVinNumber())) {
                throw new BadRequestException("Vehicle with VIN number " + 
                                             request. getVinNumber() + " already exists");
            }
            vehicle.setVinNumber(request. getVinNumber());
        }
        
        // Update other fields if provided
        if (request.getMake() != null) {
            vehicle.setMake(request. getMake());
        }
        
        if (request.getModel() != null) {
            vehicle. setModel(request.getModel());
        }
        
        if (request.getYearOfManufacture() != null) {
            vehicle.setYearOfManufacture(request.getYearOfManufacture());
        }
        
        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }
        
        if (request.getStatus() != null) {
            vehicle.setStatus(request.getStatus());
        }
        
        if (request.getNotes() != null) {
            vehicle.setNotes(request. getNotes());
        }
        
        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        
        log.info("Vehicle updated successfully:  {}", vehicleId);
        
        return mapToResponse(updatedVehicle);
    }
    
    /**
     * Delete vehicle
     */
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        log.info("Deleting vehicle ID: {}", vehicleId);
        
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        
        if (!vehicleOptional.isPresent()) {
            throw new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        
        vehicleRepository.deleteById(vehicleId);
        
        log.info("Vehicle deleted successfully: {}", vehicleId);
    }
    
    /**
     * Map Vehicle entity to VehicleResponse DTO
     */
    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                . id(vehicle.getId())
                .customerId(vehicle.getCustomerId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .yearOfManufacture(vehicle. getYearOfManufacture())
                .color(vehicle. getColor())
                .vinNumber(vehicle.getVinNumber())
                .status(vehicle.getStatus())
                .notes(vehicle.getNotes())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}