package com.vehiclemanagement.apigateway.dto.request;


import lombok.AllArgsConstructor;
import lombok. Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request for bill notification email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillNotificationRequest {
    private String customerName;
    private String customerEmail;
    private Long serviceRequestId;
    private String billNumber;
    private String vehicleInfo;
    private BigDecimal laborCost;
    private BigDecimal partsCost;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private String completedDate;
    private List<PartUsed> partsUsed;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartUsed {
        private String partName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}