package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceBayRequest;
import com.vehiclemanagement.servicemanagement.dto. response.ServiceBayResponse;
import com.vehiclemanagement. servicemanagement.entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.repository.ServiceBayRepository;
import org.junit.jupiter.api. BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api. Assertions.*;
import static org. mockito.ArgumentMatchers. any;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBayServiceTest_Create {

    @Mock
    private ServiceBayRepository serviceBayRepository;

    @InjectMocks
    private ServiceBayService serviceBayService;

    private ServiceBay serviceBay;

    @BeforeEach
    void setUp() {
        serviceBay = new ServiceBay();
        serviceBay.setId(1L);
        serviceBay. setBayNumber("BAY-01");
        serviceBay.setBayName("Main Bay");
        serviceBay.setIsAvailable(true);
        serviceBay.setIsActive(true);
    }

    @Test
    void createServiceBay_Success() {
        CreateServiceBayRequest request = new CreateServiceBayRequest();
        request.setBayNumber("BAY-01");
        request.setBayName("Main Bay");

        when(serviceBayRepository. existsByBayNumber("BAY-01")).thenReturn(false);
        when(serviceBayRepository.save(any(ServiceBay.class))).thenReturn(serviceBay);

        ServiceBayResponse response = serviceBayService.createServiceBay(request);

        assertNotNull(response);
        assertEquals("BAY-01", response.getBayNumber());
        assertEquals("Main Bay", response.getBayName());
        assertTrue(response.getIsAvailable());
        assertTrue(response.getIsActive());
        verify(serviceBayRepository).save(any(ServiceBay. class));
    }

    @Test
    void createServiceBay_DuplicateBayNumber() {
        CreateServiceBayRequest request = new CreateServiceBayRequest();
        request.setBayNumber("BAY-01");
        request.setBayName("Main Bay");

        when(serviceBayRepository.existsByBayNumber("BAY-01")).thenReturn(true);

        assertThrows(BadRequestException.class,
            () -> serviceBayService.createServiceBay(request));

        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void createServiceBay_WithLongName() {
        CreateServiceBayRequest request = new CreateServiceBayRequest();
        request.setBayNumber("BAY-02");
        request.setBayName("Very Long Service Bay Name For Testing");

        serviceBay.setBayNumber("BAY-02");
        serviceBay.setBayName("Very Long Service Bay Name For Testing");

        when(serviceBayRepository.existsByBayNumber("BAY-02")).thenReturn(false);
        when(serviceBayRepository.save(any(ServiceBay.class))).thenReturn(serviceBay);

        ServiceBayResponse response = serviceBayService.createServiceBay(request);

        assertNotNull(response);
        assertEquals("BAY-02", response.getBayNumber());
    }

    @Test
    void createServiceBay_WithNullName() {
        CreateServiceBayRequest request = new CreateServiceBayRequest();
        request.setBayNumber("BAY-03");
        request.setBayName(null);

        serviceBay.setBayNumber("BAY-03");
        serviceBay.setBayName(null);

        when(serviceBayRepository.existsByBayNumber("BAY-03")).thenReturn(false);
        when(serviceBayRepository.save(any(ServiceBay.class))).thenReturn(serviceBay);

        ServiceBayResponse response = serviceBayService.createServiceBay(request);

        assertNotNull(response);
        assertNull(response.getBayName());
    }
}