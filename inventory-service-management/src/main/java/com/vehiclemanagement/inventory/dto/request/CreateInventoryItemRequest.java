package com.vehiclemanagement.inventory.dto.request;

//public class CreateInventoryItemRequest {
//
//}

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private BigDecimal unitPrice;
    
    @NotBlank(message = "Unit is required")
    @Size(max = 20, message = "Unit must not exceed 20 characters")
    private String unit;
}