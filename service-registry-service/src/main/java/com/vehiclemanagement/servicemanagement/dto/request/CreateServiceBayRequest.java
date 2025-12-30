package com.vehiclemanagement.servicemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateServiceBayRequest {
	@NotBlank(message="Bay number is required")
	private String bayNumber;
	
	private String bayName;
}
