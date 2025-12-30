package com.vehiclemanagement.servicemanagement.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTechnicianRequest {
	
	@NotNull(message="Technician ID is required")
	private Long technicianId;
	@NotBlank(message="bay number is required")
	private String bayNumber;
	 @NotNull(message = "Labor cost is required")
	@DecimalMin(value = "0.0", message = "Labor cost must be positive")
	private BigDecimal laborCost;
}
