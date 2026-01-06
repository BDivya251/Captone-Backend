package com.vehiclemanagement.servicemanagement. dto.request;

import lombok. AllArgsConstructor;
import lombok.Data;
import lombok. NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long billId;
    private BigDecimal amount;
    private String currency;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}