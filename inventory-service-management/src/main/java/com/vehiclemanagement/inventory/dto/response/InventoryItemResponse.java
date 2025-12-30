package com.vehiclemanagement.inventory.dto.response;

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
public class InventoryItemResponse {
    private Long id;
    private String partNumber;
    private String partName;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}