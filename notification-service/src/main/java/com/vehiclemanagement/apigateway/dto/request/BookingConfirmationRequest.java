package com.vehiclemanagement.apigateway.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for booking confirmation email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingConfirmationRequest {
    private String customerName;
    private String customerEmail;
    private Long serviceRequestId;
    private String requestType; // REPAIR, MAINTENANCE, INSPECTION
    private String vehicleInfo; // "Toyota Camry 2020"
    private String description;
    private String requestDate;
}