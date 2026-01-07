package com.vehiclemanagement. servicemanagement.service;

import com.vehiclemanagement.servicemanagement.entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.exception.ResourceNotFoundException;
import com. vehiclemanagement.servicemanagement.repository.ServiceBayRepository;
import org.junit.jupiter. api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api. Assertions.*;
import static org. mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBayServiceTest_Update {

    @Mock
    private ServiceBayRepository serviceBayRepository;

    @InjectMocks
    private ServiceBayService serviceBayService;

    private ServiceBay serviceBay;

    @BeforeEach
    void setUp() {
        serviceBay = new ServiceBay();
        serviceBay. setId(1L);
        serviceBay.setBayNumber("BAY-01");
        serviceBay.setBayName("Main Bay");
        serviceBay. setIsAvailable(true);
        serviceBay.setIsActive(true);
    }

    @Test
    void allocateBay_Success() {
        when(serviceBayRepository.findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay));
        when(serviceBayRepository. save(any(ServiceBay. class)))
            .thenReturn(serviceBay);

        serviceBayService.allocateBay("BAY-01", 100L);

        verify(serviceBayRepository).save(any(ServiceBay.class));
    }

    @Test
    void allocateBay_BayNotFound() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceBayService. allocateBay("BAY-99", 100L));

        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void allocateBay_BayAlreadyInUse() {
        serviceBay.setIsAvailable(false);
        serviceBay.setCurrentServiceRequestId(50L);

        when(serviceBayRepository.findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay));

        assertThrows(BadRequestException.class,
            () -> serviceBayService.allocateBay("BAY-01", 100L));

        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void allocateBay_BayNotActive() {
        serviceBay. setIsActive(false);

        when(serviceBayRepository. findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay));

        assertThrows(BadRequestException.class,
            () -> serviceBayService.allocateBay("BAY-01", 100L));

        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void releaseBay_Success() {
        serviceBay.setIsAvailable(false);
        serviceBay.setCurrentServiceRequestId(100L);

        when(serviceBayRepository.findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay));
        when(serviceBayRepository.save(any(ServiceBay.class)))
            .thenReturn(serviceBay);

        serviceBayService.releaseBay("BAY-01");

        verify(serviceBayRepository).save(any(ServiceBay.class));
    }

    @Test
    void releaseBay_BayNotFound() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
            .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> serviceBayService.releaseBay("BAY-99"));

        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void releaseBay_AlreadyAvailable() {
        when(serviceBayRepository.findByBayNumber("BAY-01"))
            .thenReturn(Optional.of(serviceBay));
        when(serviceBayRepository.save(any(ServiceBay.class)))
            .thenReturn(serviceBay);

        serviceBayService.releaseBay("BAY-01");

        verify(serviceBayRepository).save(any(ServiceBay. class));
    }
}