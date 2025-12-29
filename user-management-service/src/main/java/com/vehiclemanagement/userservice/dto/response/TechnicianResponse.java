package com.vehiclemanagement.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianResponse {
    private Long id;
    private Long userId;
    private String name;
    private String skillSet;
    private String phone;
}