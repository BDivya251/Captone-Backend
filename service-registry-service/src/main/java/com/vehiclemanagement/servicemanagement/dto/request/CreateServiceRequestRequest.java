package com.vehiclemanagement.servicemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequestRequest {
	@NotNull(message="CustomerId is required")
	private Long CustomerId;
	@NotNull(message="VehicleId is required")
	private Long vehicleId;
	@NotBlank(message="requestType is required")
	private String requestType; //Repair,Maintenance,Inception
	@NotBlank(message="Desciption is required")
	private String description;
}
