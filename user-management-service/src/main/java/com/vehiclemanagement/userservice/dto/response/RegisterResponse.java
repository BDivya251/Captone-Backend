package com.vehiclemanagement.userservice.dto.response;

import java.util.List;

import com.vehiclemanagement.userservice.enums.UserRole;
import com.vehiclemanagement.userservice.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
	private Long userId;
    private String email;
    private String role;
    private String status;
    private String message;
    
}
