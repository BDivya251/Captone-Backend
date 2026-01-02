package com.vehiclemanagement.vehicle. controller;

import com.vehiclemanagement.vehicle.dto.request.CreateVehicleRequest;
import com.vehiclemanagement.vehicle.dto. request.UpdateVehicleRequest;
import com.vehiclemanagement.vehicle.dto.response.VehicleResponse;
import com.vehiclemanagement.vehicle.entity.Vehicle;
import com.vehiclemanagement.vehicle.enums.VehicleStatus;
import com.vehiclemanagement.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework. http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    
    @PostMapping
    public ResponseEntity<Void> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        log.info("POST /api/vehicles - Create vehicle request received");
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    
    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {
        log.info("GET /api/vehicles - Fetch all vehicles");
        List<VehicleResponse> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity. ok(vehicles);
    }
    
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long vehicleId) {
        log.info("GET /api/vehicles/{} - Fetch vehicle by ID", vehicleId);
        VehicleResponse vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }
    
   
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<VehicleResponse>> getVehiclesByCustomerId(@PathVariable Long customerId) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByCustomerId(customerId);
        return ResponseEntity.ok(vehicles);
    }
    
    
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long vehicleId,
            @Valid @RequestBody UpdateVehicleRequest request) {
        
        log.info("PUT /api/vehicles/{} - Update vehicle request received", vehicleId);
        VehicleResponse response = vehicleService.updateVehicle(vehicleId, request);
        return ResponseEntity.ok(response);
    }
    
    
    
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Map<String, String>> deleteVehicle(@PathVariable Long vehicleId) {
        log.info("DELETE /api/vehicles/{} - Delete vehicle request received", vehicleId);
        
        vehicleService.deleteVehicle(vehicleId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Vehicle deleted successfully");
        response.put("vehicleId", vehicleId.toString());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/status")
    public ResponseEntity<VehicleStatus>  UpdateStatusCode(@RequestParam VehicleStatus status,@RequestParam String regsitration){
    	VehicleStatus a=vehicleService.changeStatusOfVehicle(status,regsitration);
    	return ResponseEntity.ok(a);
    }
}