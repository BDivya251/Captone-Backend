package com.vehiclemanagement.servicemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceBillResponse {
    private Long id;
    private String billNumber;
    private BigDecimal laborCost;
    private BigDecimal partsCost;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private LocalDateTime generatedDate;
}