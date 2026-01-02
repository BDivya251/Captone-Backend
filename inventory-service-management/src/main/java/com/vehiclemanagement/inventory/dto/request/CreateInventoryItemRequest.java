package com.vehiclemanagement.inventory.dto.request;

//public class CreateInventoryItemRequest {
//
//}

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.vehiclemanagement.inventory.enums.UnitType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInventoryItemRequest {
    
    @NotBlank(message = "Part number is required")
    @Size(max = 50, message = "Part number must not exceed 50 characters")
    private String partNumber;
    
    @NotBlank(message = "Part name is required")
    @Size(max = 200, message = "Part name must not exceed 200 characters")
    private String partName;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Unit price must have up to 8 digits and 2 decimal places")
    private BigDecimal unitPrice;
    
    @NotNull(message = "Unit is required")
    private UnitType unit;
}