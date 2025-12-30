package com.vehiclemanagement.servicemanagement.feign;

import lombok. Data;
import java.math.BigDecimal;

@Data
public class InventoryItemResponse {
    private Long id;
    private String partNumber;
    private String partName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String unit;
}