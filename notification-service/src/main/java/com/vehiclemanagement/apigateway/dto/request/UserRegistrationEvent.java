package com.vehiclemanagement.apigateway.dto.request;


import lombok.AllArgsConstructor;
import lombok. Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * User Registration Event
 * Sent from User Service when user registers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationEvent implements Serializable {
    private Long userId;
    private String email;
    private String name;
    private String role; // ADMIN, MANAGER, TECHNICIAN, CUSTOMER
    private String registrationDate;
}