package com.vehiclemanagement.servicemanagement.feign;

import lombok. Data;

@Data
public class TechnicianResponse {
    private Long id;
    private Long userId;
    private String name;
    private String skillSet;
    private String phone;
}