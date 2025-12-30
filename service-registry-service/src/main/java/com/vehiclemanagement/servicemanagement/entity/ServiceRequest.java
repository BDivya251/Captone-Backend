package com.vehiclemanagement. servicemanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;
    
    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType; // REPAIR, MAINTENANCE, INSPECTION
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    
    @Column(name = "manager_id")
    private Long managerId;
    
    @Column(name = "technician_id")
    private Long technicianId;
    
    @Column(name = "bay_number", length = 20)
    private String bayNumber;
    
    @Column(name = "is_bay_allocated")
    private Boolean isBayAllocated = false;
    
    @Column(length = 2000)
    private String remarks;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "request_date")
    private LocalDateTime requestDate = LocalDateTime.now();
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    @Column(name = "labor_cost", precision = 10, scale = 2)
    private BigDecimal laborCost;
    
    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL)
    private List<ServiceImage> images = new ArrayList<>();
    
    @OneToMany(mappedBy = "serviceRequest", cascade = CascadeType.ALL)
    private List<InventoryUsage> inventoryUsages = new ArrayList<>();
    
    @OneToOne(mappedBy = "serviceRequest", cascade = CascadeType.ALL)
    private ServiceBill bill;
}