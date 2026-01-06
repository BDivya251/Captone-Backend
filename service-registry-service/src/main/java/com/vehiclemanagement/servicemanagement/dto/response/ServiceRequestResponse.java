package com. vehiclemanagement.servicemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequestResponse {
    private Long id;
    private Long customerId;
    private Long vehicleId;
    private String requestType;
    private String description;
    private String status;
    private Long managerId;
    private Long technicianId;
    private String bayNumber;
    private Boolean isBayAllocated;
    private String remarks;
    private BigDecimal totalAmount;
    private LocalDateTime requestDate;
    private LocalDateTime completedDate;
    private List<String> imageIds;
    private BigDecimal laborCost;
    private List<InventoryUsageResponse> inventoryUsages;
    private ServiceBillResponse bill;
  
}