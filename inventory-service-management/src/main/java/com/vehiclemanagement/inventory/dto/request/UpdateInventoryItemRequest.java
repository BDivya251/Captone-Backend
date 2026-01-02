package com.vehiclemanagement.inventory.dto.request;


import jakarta.validation. constraints. DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.vehiclemanagement.inventory.enums.UnitType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryItemRequest {
    
    @Size(max = 200, message = "Part name must not exceed 200 characters")
    private String partName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Unit is required")
    private UnitType unit;
}