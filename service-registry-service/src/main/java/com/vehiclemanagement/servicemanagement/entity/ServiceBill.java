package com.vehiclemanagement.servicemanagement. entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math. BigDecimal;
import java. time.LocalDateTime;

@Entity
@Table(name = "service_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequest serviceRequest;
    
    @Column(name = "bill_number", unique = true, length = 50)
    private String billNumber;
    
    @Column(name = "labor_cost", precision = 10, scale = 2)
    private BigDecimal laborCost = BigDecimal.ZERO;
    
    @Column(name = "parts_cost", precision = 10, scale = 2)
    private BigDecimal partsCost = BigDecimal.ZERO;
    
    @Column(name = "tax", precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "generated_date")
    private LocalDateTime generatedDate = LocalDateTime.now();
}