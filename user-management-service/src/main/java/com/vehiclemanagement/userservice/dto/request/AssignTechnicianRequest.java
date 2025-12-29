package com.vehiclemanagement.userservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTechnicianRequest {
    
    @NotNull(message = "Manager ID is required")
    private Long managerId;
    
    @NotNull(message = "Technician ID is required")
    private Long technicianId;
}