package com. vehiclemanagement.userservice. dto.response;

import lombok. AllArgsConstructor;
import lombok.Builder;
import lombok. Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianStatsResponse {
    private Long technicianId;
    private Integer assigned;
    private Integer completed;
    private Integer inProgress;
    private Integer pending;
}