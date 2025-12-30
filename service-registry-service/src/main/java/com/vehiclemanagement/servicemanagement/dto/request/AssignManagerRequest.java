package com.vehiclemanagement.servicemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignManagerRequest {
	@NotNull(message="managerId is required")
	private Long managerId;
}
