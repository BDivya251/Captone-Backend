package com.vehiclemanagement.servicemanagement.dto.request;

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
}
