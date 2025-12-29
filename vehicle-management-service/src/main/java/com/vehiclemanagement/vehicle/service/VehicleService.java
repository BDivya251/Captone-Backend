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
    
    
    private void validateCustomerExists(Long customerId) {
        log.info("Validating customer ID: {} with User Service", customerId);
        
        try {
            CustomerResponse customer = userServiceClient.getCustomerById(customerId);
            
        } catch (FeignException. NotFound ex) {
            
            throw new BadRequestException("Customer with ID " + customerId + " does not exist");
        } catch (FeignException ex) {
           
            throw new FeignClientException("Unable to validate customer.  User Service may be unavailable.", ex);
        }
    }
    
  
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        
        validateCustomerExists(request.getCustomerId());
        
        
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new BadRequestException("Vehicle with registration number " + 
                                         request.getRegistrationNumber() + " already exists");
        }
        
      
        if (request.getVinNumber() != null && !request.getVinNumber().trim().isEmpty()) {
            if (vehicleRepository.existsByVinNumber(request.getVinNumber())) {
                throw new BadRequestException("Vehicle with VIN number " + 
                                             request.getVinNumber() + " already exists");
            }
        }
        
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
    
    
    public VehicleResponse getVehicleById(Long vehicleId) {
        log.info("Fetching vehicle by ID: {}", vehicleId);
        
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        
        if (! vehicleOptional.isPresent()) {
            throw new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId);
        }
        
        Vehicle vehicle = vehicleOptional.get();
        return mapToResponse(vehicle);
    }
    
    
    public List<VehicleResponse> getVehiclesByCustomerId(Long customerId) {
        log.info("Fetching vehicles for customer ID: {}", customerId);
        
        
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
    
   
    @Transactional
    public VehicleResponse updateVehicle(Long vehicleId, UpdateVehicleRequest request) {
        log.info("Updating vehicle ID: {}", vehicleId);
        
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        
        if (!vehicleOptional.isPresent()) {
            throw new ResourceNotFoundException("Vehicle not found with ID:  " + vehicleId);
        }
        
        Vehicle vehicle = vehicleOptional.get();
        
       
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