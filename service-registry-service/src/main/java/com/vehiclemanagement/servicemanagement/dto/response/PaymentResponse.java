package com.vehiclemanagement.servicemanagement. dto.response;

import lombok. AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String orderId;           
    private String razorpayKeyId;     
    private BigDecimal amount;
    private String currency;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String companyName;
    private String description;
}