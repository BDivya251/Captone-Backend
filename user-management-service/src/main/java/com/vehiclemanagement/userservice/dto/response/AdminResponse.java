package com.vehiclemanagement.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponse {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String status;
}