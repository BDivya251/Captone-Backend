package com.vehiclemanagement.vehicle.dto.request;

import com.vehiclemanagement.vehicle.enums.VehicleStatus;
import jakarta.validation. constraints.Min;
import jakarta. validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {
    
    @Size(max = 50, message = "Registration number must not exceed 50 characters")
    private String registrationNumber;
    
    @Size(max = 100, message = "Make must not exceed 100 characters")
    private String make;
    
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;
    
    @Min(value = 1900, message = "Year must be 1900 or later")
    private Integer yearOfManufacture;
    
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;
    
    @Size(max = 50, message = "VIN number must not exceed 50 characters")
    private String vinNumber;
    
    private VehicleStatus status;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}