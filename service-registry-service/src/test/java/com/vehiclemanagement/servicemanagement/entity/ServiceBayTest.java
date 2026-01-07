package com.vehiclemanagement.servicemanagement. entity;

import org.junit. jupiter.api.Test;
import static org.junit.jupiter.api. Assertions.*;

class ServiceBayTest {

    @Test
    void testEntityCreation() {
        ServiceBay bay = new ServiceBay();
        bay.setId(1L);
        bay.setBayNumber("BAY-01");
        bay.setBayName("Main Service Bay");
        bay.setIsAvailable(true);
        bay.setIsActive(true);
        bay.setCurrentServiceRequestId(null);

        assertEquals(1L, bay.getId());
        assertEquals("BAY-01", bay.getBayNumber());
        assertEquals("Main Service Bay", bay.getBayName());
        assertTrue(bay.getIsAvailable());
        assertTrue(bay.getIsActive());
        assertNull(bay.getCurrentServiceRequestId());
    }

    @Test
    void testAllArgsConstructor() {
        ServiceBay bay = new ServiceBay(1L, "BAY-02", "Bay 2", true, null, true);
        
        assertNotNull(bay);
        assertEquals("BAY-02", bay.getBayNumber());
        assertTrue(bay.getIsAvailable());
    }

    @Test
    void testNoArgsConstructor() {
        ServiceBay bay = new ServiceBay();
        
        assertNotNull(bay);
        assertNull(bay.getBayNumber());
    }

    @Test
    void testSettersAndGetters() {
        ServiceBay bay = new ServiceBay();
        
        bay.setId(5L);
        bay.setBayNumber("BAY-05");
        bay.setBayName("Service Bay 5");
        bay.setIsAvailable(false);
        bay.setIsActive(true);
        bay.setCurrentServiceRequestId(100L);

        assertEquals(5L, bay. getId());
        assertEquals("BAY-05", bay.getBayNumber());
        assertEquals("Service Bay 5", bay.getBayName());
        assertFalse(bay.getIsAvailable());
        assertTrue(bay.getIsActive());
        assertEquals(100L, bay.getCurrentServiceRequestId());
    }

    @Test
    void testDefaultValues() {
        ServiceBay bay = new ServiceBay();
        bay.setIsAvailable(true);
        bay.setIsActive(true);

        assertTrue(bay.getIsAvailable());
        assertTrue(bay. getIsActive());
    }

    @Test
    void testBayInUse() {
        ServiceBay bay = new ServiceBay();
        bay.setIsAvailable(false);
        bay.setCurrentServiceRequestId(200L);

        assertFalse(bay.getIsAvailable());
        assertEquals(200L, bay.getCurrentServiceRequestId());
    }

    @Test
    void testBayDeactivated() {
        ServiceBay bay = new ServiceBay();
        bay.setIsActive(false);
        bay.setIsAvailable(true);

        assertFalse(bay.getIsActive());
        assertTrue(bay.getIsAvailable());
    }
}