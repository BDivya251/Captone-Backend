package com.vehiclemanagement.servicemanagement.feign;

import lombok.Data;

@Data
public class VehicleResponse {
    private Long id;
    private Long customerId;
    private String registrationNumber;
    private String make;
    private String model;
}