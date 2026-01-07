package com.vehiclemanagement.servicemanagement. service;

import com.vehiclemanagement.servicemanagement.entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.exception.ResourceNotFoundException;
import com.vehiclemanagement.servicemanagement.repository.ServiceBayRepository;
import org.junit.jupiter.api. BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBayServiceTest_Delte {

    @Mock
    private ServiceBayRepository serviceBayRepository;

    @InjectMocks
    private ServiceBayService serviceBayService;

    private ServiceBay serviceBay;

    @BeforeEach
    void setUp() {
        serviceBay = new ServiceBay();
        serviceBay.setId(1L);
        serviceBay.setBayNumber("BAY-01");
        serviceBay.setBayName("Main Bay");
        serviceBay.setIsAvailable(true);
        serviceBay.setIsActive(true);
    }

    @Test
    void deleteServiceBay_Success() {
        when(serviceBayRepository.findById(1L))
            .thenReturn(Optional.of(serviceBay));
        doNothing().when(serviceBayRepository).deleteById(1L);

        serviceBayService.deleteServiceBay(1L);

        verify(serviceBayRepository).deleteById(1L);
    }

    @Test
    void deleteServiceBay_NotFound() {
        when(serviceBayRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceBayService.deleteServiceBay(1L));

        verify(serviceBayRepository, never()).deleteById(any());
    }

    @Test
    void deleteServiceBay_BayInUse() {
        serviceBay.setIsAvailable(false);
        serviceBay.setCurrentServiceRequestId(100L);

        when(serviceBayRepository.findById(1L))
            .thenReturn(Optional. of(serviceBay));

        assertThrows(BadRequestException. class,
            () -> serviceBayService.deleteServiceBay(1L));

        verify(serviceBayRepository, never()).deleteById(any());
    }

    @Test
    void deleteServiceBay_InactiveButAvailable() {
        serviceBay.setIsActive(false);
        serviceBay.setIsAvailable(true);

        when(serviceBayRepository.findById(1L))
            .thenReturn(Optional.of(serviceBay));
        doNothing().when(serviceBayRepository).deleteById(1L);

        serviceBayService.deleteServiceBay(1L);

        verify(serviceBayRepository).deleteById(1L);
    }
}