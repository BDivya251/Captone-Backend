package com.vehiclemanagement.servicemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceBayResponse {
	private Long id;
    private String bayNumber;
    private String bayName;
    private Boolean isAvailable;
    private Long currentServiceRequestId;
    private Boolean isActive;
}
