package com.vehiclemanagement.inventory.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate. annotations.UpdateTimestamp;

import com.vehiclemanagement.inventory.enums.UnitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long id;
    
    @Column(name = "part_number", nullable = false, unique = true, length = 50)
    private String partNumber;
    
    @Column(name = "part_name", nullable = false, length = 200)
    private String partName;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;
    
    @Column(name = "unit_price", nullable = false, precision = 2)
    private BigDecimal unitPrice;
    
    @Enumerated(EnumType.STRING)
    private UnitType unit;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
