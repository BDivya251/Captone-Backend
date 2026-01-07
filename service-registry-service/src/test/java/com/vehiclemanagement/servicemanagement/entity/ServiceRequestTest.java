package com.vehiclemanagement.servicemanagement.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter. api.Assertions.*;

class ServiceRequestTest {

    @Test
    void testEntityCreation() {
        ServiceRequest request = new ServiceRequest();
        request.setId(1L);
        request.setCustomerId(100L);
        request.setVehicleId(200L);
        request.setRequestType("REPAIR");
        request.setDescription("Engine issue");
        request.setStatus("PENDING");
        request.setLaborCost(new BigDecimal("500"));

        assertEquals("REPAIR", request.getRequestType());
        assertEquals("PENDING", request.getStatus());
    }

    @Test
    void testDefaultValues() {
        ServiceRequest request = new ServiceRequest();
        assertEquals("PENDING", request.getStatus());
        assertFalse(request.getIsBayAllocated());
    }
}