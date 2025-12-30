package com.vehiclemanagement.servicemanagement.feign;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String email;
}