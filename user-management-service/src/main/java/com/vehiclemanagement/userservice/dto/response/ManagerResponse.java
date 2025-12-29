package com.vehiclemanagement.userservice.dto. response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok. Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerResponse {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private List<TechnicianResponse> technicians;
}