package com.vehiclemanagement.servicemanagement.dto.request;

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
	@NotNull(message="requestType is required")
	private String requestType; //Repair,Maintenance,Inception
	@NotNull(message="Desciption is required")
	private String description;
}
