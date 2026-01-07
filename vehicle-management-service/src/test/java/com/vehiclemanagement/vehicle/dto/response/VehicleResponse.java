package com.vehiclemanagement.vehicle.dto.response;

import com.vehiclemanagement.vehicle.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private Long id;
    private Long customerId;
    private String registrationNumber;
    private String make;
    private String model;
    private Integer yearOfManufacture;
    private String color;
    private String vinNumber;
    private VehicleStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}