package com.vehiclemanagement.servicemanagement.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api. Assertions.*;

class InventoryUsageTest {

    @Test
    void testEntityCreation() {
        InventoryUsage usage = new InventoryUsage();
        usage.setId(1L);
        usage.setInventoryItemId(10L);
        usage.setPartName("Oil Filter");
        usage.setQuantity(2);
        usage.setUnitPrice(new BigDecimal("100.00"));
        usage.setTotalPrice(new BigDecimal("200.00"));

        assertEquals(1L, usage. getId());
        assertEquals("Oil Filter", usage.getPartName());
        assertEquals(2, usage.getQuantity());
    }

    @Test
    void testAllArgsConstructor() {
        ServiceRequest sr = new ServiceRequest();
        InventoryUsage usage = new InventoryUsage(1L, sr, 10L, "Part", 5, 
            new BigDecimal("50"), new BigDecimal("250"));
        
        assertNotNull(usage);
        assertEquals(5, usage.getQuantity());
    }
}