package com.vehiclemanagement.servicemanagement. service;

import com.vehiclemanagement.servicemanagement.dto.response.ServiceBayResponse;
import com.vehiclemanagement.servicemanagement.entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception. ResourceNotFoundException;
import com.vehiclemanagement.servicemanagement. repository.ServiceBayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api. Test;
import org.junit. jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit. jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBayServiceTest_Read {

    @Mock
    private ServiceBayRepository serviceBayRepository;

    @InjectMocks
    private ServiceBayService serviceBayService;

    private ServiceBay serviceBay1;
    private ServiceBay serviceBay2;

    @BeforeEach
    void setUp() {
        serviceBay1 = new ServiceBay();
        serviceBay1.setId(1L);
        serviceBay1.setBayNumber("BAY-01");
        serviceBay1.setBayName("Main Bay");
        serviceBay1.setIsAvailable(true);
        serviceBay1.setIsActive(true);

        serviceBay2 = new ServiceBay();
        serviceBay2.setId(2L);
        serviceBay2.setBayNumber("BAY-02");
        serviceBay2.setBayName("Secondary Bay");
        serviceBay2.setIsAvailable(false);
        serviceBay2.setIsActive(true);
        serviceBay2.setCurrentServiceRequestId(100L);
    }

    @Test
    void getAllServiceBays_Success() {
        when(serviceBayRepository.findAll()).thenReturn(Arrays.asList(serviceBay1, serviceBay2));

        List<ServiceBayResponse> response = serviceBayService.getAllServiceBays();

        assertEquals(2, response.size());
        assertEquals("BAY-01", response. get(0).getBayNumber());
        assertEquals("BAY-02", response.get(1).getBayNumber());
    }

    @Test
    void getAllServiceBays_Empty() {
        when(serviceBayRepository.findAll()).thenReturn(Arrays.asList());

        List<ServiceBayResponse> response = serviceBayService.getAllServiceBays();

        assertEquals(0, response.size());
    }

    @Test
    void getAvailableServiceBays_Success() {
        when(serviceBayRepository.findByIsAvailableAndIsActive(true, true))
            .thenReturn(Arrays.asList(serviceBay1));

        List<ServiceBayResponse> response = serviceBayService. getAvailableServiceBays();

        assertEquals(1, response. size());
        assertEquals("BAY-01", response.get(0).getBayNumber());
        assertTrue(response.get(0).getIsAvailable());
    }

    @Test
    void getAvailableServiceBays_NoneAvailable() {
        when(serviceBayRepository.findByIsAvailableAndIsActive(true, true))
            .thenReturn(Arrays.asList());

        List<ServiceBayResponse> response = serviceBayService. getAvailableServiceBays();

        assertEquals(0, response. size());
    }

    @Test
    void getBayByNumber_Success() {
        when(serviceBayRepository. findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay1));

        ServiceBay result = serviceBayService.getBayByNumber("BAY-01");

        assertNotNull(result);
        assertEquals("BAY-01", result.getBayNumber());
    }

    @Test
    void getBayByNumber_NotFound() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceBayService. getBayByNumber("BAY-99"));
    }

    @Test
    void isBayAvailable_True() {
        when(serviceBayRepository.findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay1));

        boolean result = serviceBayService.isBayAvailable("BAY-01");

        assertTrue(result);
    }

    @Test
    void isBayAvailable_False_InUse() {
        when(serviceBayRepository.findByBayNumber("BAY-02"))
            .thenReturn(Optional.of(serviceBay2));

        boolean result = serviceBayService.isBayAvailable("BAY-02");

        assertFalse(result);
    }

    @Test
    void isBayAvailable_False_Inactive() {
        serviceBay1.setIsActive(false);

        when(serviceBayRepository. findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay1));

        boolean result = serviceBayService.isBayAvailable("BAY-01");

        assertFalse(result);
    }

    @Test
    void isBayAvailable_BayNotFound() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceBayService.isBayAvailable("BAY-99"));
    }
}