package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceBayRequest;
import com.vehiclemanagement. servicemanagement.dto.response. ServiceBayResponse;
import com.vehiclemanagement.servicemanagement.entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement. exception.ResourceNotFoundException;
import com.vehiclemanagement.servicemanagement.repository.ServiceBayRepository;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org. mockito.Mock;
import org.mockito.junit.jupiter. MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBayServiceTest {

    @Mock
    private ServiceBayRepository serviceBayRepository;

    @InjectMocks
    private ServiceBayService serviceBayService;

    private ServiceBay testBay;
    private CreateServiceBayRequest createRequest;

    @BeforeEach
    void setUp() {
        // Setup test bay
        testBay = new ServiceBay();
        testBay.setId(1L);
        testBay.setBayNumber("BAY-01");
        testBay.setBayName("Main Service Bay 1");
        testBay.setIsAvailable(true);
        testBay.setIsActive(true);
        testBay.setCurrentServiceRequestId(null);

        // Setup create request
        createRequest = new CreateServiceBayRequest();
        createRequest. setBayNumber("BAY-02");
        createRequest.setBayName("Service Bay 2");
    }
    
    @Test
    void createServiceBay_Success() {
        when(serviceBayRepository.existsByBayNumber("BAY-02")).thenReturn(false);
        when(serviceBayRepository. save(any(ServiceBay. class))).thenReturn(testBay);

        ServiceBayResponse response = serviceBayService. createServiceBay(createRequest);

        assertNotNull(response);
        assertEquals("BAY-01", response.getBayNumber());
        assertTrue(response.getIsAvailable());
        assertTrue(response.getIsActive());
        
        verify(serviceBayRepository).existsByBayNumber("BAY-02");
        verify(serviceBayRepository).save(any(ServiceBay.class));
    }

    @Test
    void createServiceBay_DuplicateBayNumber_ThrowsBadRequestException() {
        when(serviceBayRepository. existsByBayNumber("BAY-02")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException. class, () -> {
            serviceBayService.createServiceBay(createRequest);
        });

        assertTrue(exception.getMessage().contains("Service bay already exists"));
        
        verify(serviceBayRepository).existsByBayNumber("BAY-02");
        verify(serviceBayRepository, never()).save(any());
    }
    @Test
    void getAllServiceBays_Success() {
        ServiceBay bay2 = new ServiceBay();
        bay2.setId(2L);
        bay2.setBayNumber("BAY-02");
        bay2.setBayName("Service Bay 2");
        bay2.setIsAvailable(false);
        bay2.setIsActive(true);

        List<ServiceBay> bays = Arrays.asList(testBay, bay2);
        when(serviceBayRepository.findAll()).thenReturn(bays);

        List<ServiceBayResponse> responses = serviceBayService.getAllServiceBays();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("BAY-01", responses.get(0).getBayNumber());
        assertEquals("BAY-02", responses.get(1).getBayNumber());
        
        verify(serviceBayRepository).findAll();
    }

    @Test
    void getAllServiceBays_EmptyList_ReturnsEmptyList() {
        when(serviceBayRepository.findAll()).thenReturn(Collections.emptyList());

        List<ServiceBayResponse> responses = serviceBayService.getAllServiceBays();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        
        verify(serviceBayRepository).findAll();
    }
    @Test
    void getAvailableServiceBays_Success() {
        List<ServiceBay> availableBays = Arrays.asList(testBay);
        when(serviceBayRepository.findByIsAvailableAndIsActive(true, true))
                .thenReturn(availableBays);

        List<ServiceBayResponse> responses = serviceBayService.getAvailableServiceBays();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("BAY-01", responses.get(0).getBayNumber());
        assertTrue(responses.get(0).getIsAvailable());
        
        verify(serviceBayRepository).findByIsAvailableAndIsActive(true, true);
    }

    @Test
    void getAvailableServiceBays_NoneAvailable_ReturnsEmptyList() {
        when(serviceBayRepository. findByIsAvailableAndIsActive(true, true))
                .thenReturn(Collections.emptyList());

        List<ServiceBayResponse> responses = serviceBayService.getAvailableServiceBays();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        
        verify(serviceBayRepository).findByIsAvailableAndIsActive(true, true);
    }
    @Test
    void getBayByNumber_Success() {
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));

        ServiceBay bay = serviceBayService.getBayByNumber("BAY-01");

        assertNotNull(bay);
        assertEquals("BAY-01", bay.getBayNumber());
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
    }

    @Test
    void getBayByNumber_NotFound_ThrowsResourceNotFoundException() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceBayService.getBayByNumber("BAY-99");
        });

        assertTrue(exception.getMessage().contains("Service bay not found"));
        
        verify(serviceBayRepository).findByBayNumber("BAY-99");
    }
    @Test
    void isBayAvailable_AvailableAndActive_ReturnsTrue() {
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));

        boolean isAvailable = serviceBayService. isBayAvailable("BAY-01");

        assertTrue(isAvailable);
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
    }

    @Test
    void isBayAvailable_NotAvailable_ReturnsFalse() {
        testBay.setIsAvailable(false);
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));

        boolean isAvailable = serviceBayService.isBayAvailable("BAY-01");

        assertFalse(isAvailable);
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
    }

    @Test
    void isBayAvailable_NotActive_ReturnsFalse() {
        testBay.setIsActive(false);
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));

        boolean isAvailable = serviceBayService.isBayAvailable("BAY-01");

        assertFalse(isAvailable);
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
    }
    @Test
    void allocateBay_Success() {
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));
        when(serviceBayRepository. save(any(ServiceBay. class))).thenReturn(testBay);

        assertDoesNotThrow(() -> {
            serviceBayService.allocateBay("BAY-01", 100L);
        });

        assertFalse(testBay.getIsAvailable());
        assertEquals(100L, testBay. getCurrentServiceRequestId());
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
        verify(serviceBayRepository).save(testBay);
    }

    @Test
    void allocateBay_AlreadyInUse_ThrowsBadRequestException() {
        testBay.setIsAvailable(false);
        testBay.setCurrentServiceRequestId(50L);
        
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            serviceBayService.allocateBay("BAY-01", 100L);
        });

        assertTrue(exception.getMessage().contains("already in use"));
        assertTrue(exception.getMessage().contains("50"));
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void allocateBay_NotActive_ThrowsBadRequestException() {
        testBay.setIsActive(false);
        
        when(serviceBayRepository. findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            serviceBayService.allocateBay("BAY-01", 100L);
        });

        assertTrue(exception.getMessage().contains("not active"));
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
        verify(serviceBayRepository, never()).save(any());
    }

    @Test
    void allocateBay_BayNotFound_ThrowsResourceNotFoundException() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceBayService.allocateBay("BAY-99", 100L);
        });
        
        verify(serviceBayRepository).findByBayNumber("BAY-99");
    }
    @Test
    void releaseBay_Success() {
        testBay.setIsAvailable(false);
        testBay.setCurrentServiceRequestId(100L);
        
        when(serviceBayRepository.findByBayNumber("BAY-01"))
                .thenReturn(Optional.of(testBay));
        when(serviceBayRepository. save(any(ServiceBay. class))).thenReturn(testBay);

        assertDoesNotThrow(() -> {
            serviceBayService.releaseBay("BAY-01");
        });

        assertTrue(testBay.getIsAvailable());
        assertNull(testBay.getCurrentServiceRequestId());
        
        verify(serviceBayRepository).findByBayNumber("BAY-01");
        verify(serviceBayRepository).save(testBay);
    }

    @Test
    void releaseBay_BayNotFound_ThrowsResourceNotFoundException() {
        when(serviceBayRepository.findByBayNumber("BAY-99"))
                .thenReturn(Optional. empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            serviceBayService.releaseBay("BAY-99");
        });
        
        verify(serviceBayRepository).findByBayNumber("BAY-99");
    }
    @Test
    void deleteServiceBay_Success() {
        when(serviceBayRepository. findById(1L)).thenReturn(Optional.of(testBay));
        doNothing().when(serviceBayRepository).deleteById(1L);

        assertDoesNotThrow(() -> {
            serviceBayService.deleteServiceBay(1L);
        });

        verify(serviceBayRepository).findById(1L);
        verify(serviceBayRepository).deleteById(1L);
    }

    @Test
    void deleteServiceBay_NotFound_ThrowsResourceNotFoundException() {
        when(serviceBayRepository.findById(999L)).thenReturn(Optional. empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            serviceBayService.deleteServiceBay(999L);
        });

        assertTrue(exception.getMessage().contains("Service bay not found"));
        
        verify(serviceBayRepository).findById(999L);
        verify(serviceBayRepository, never()).deleteById(any());
    }

    @Test
    void deleteServiceBay_BayInUse_ThrowsBadRequestException() {
        testBay.setIsAvailable(false);
        testBay.setCurrentServiceRequestId(100L);
        
        when(serviceBayRepository.findById(1L)).thenReturn(Optional.of(testBay));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            serviceBayService.deleteServiceBay(1L);
        });

        assertTrue(exception. getMessage().contains("Cannot delete bay that is currently in use"));
        
        verify(serviceBayRepository).findById(1L);
        verify(serviceBayRepository, never()).deleteById(any());
    }

    @Test
    void createServiceBay_WithNullBayName_Success() {
        createRequest.setBayName(null);
        
        when(serviceBayRepository.existsByBayNumber("BAY-02")).thenReturn(false);
        when(serviceBayRepository. save(any(ServiceBay. class))).thenReturn(testBay);

        ServiceBayResponse response = serviceBayService. createServiceBay(createRequest);

        assertNotNull(response);
        verify(serviceBayRepository).save(any(ServiceBay.class));
    }

    @Test
    void getAllServiceBays_MultipleBaysWithDifferentStatuses_Success() {
        ServiceBay bay2 = new ServiceBay();
        bay2.setId(2L);
        bay2.setBayNumber("BAY-02");
        bay2.setIsAvailable(false);
        bay2.setCurrentServiceRequestId(50L);
        bay2.setIsActive(true);

        ServiceBay bay3 = new ServiceBay();
        bay3.setId(3L);
        bay3.setBayNumber("BAY-03");
        bay3.setIsAvailable(true);
        bay3.setIsActive(false);

        List<ServiceBay> bays = Arrays.asList(testBay, bay2, bay3);
        when(serviceBayRepository.findAll()).thenReturn(bays);

        List<ServiceBayResponse> responses = serviceBayService.getAllServiceBays();

        assertEquals(3, responses.size());
        assertTrue(responses.get(0).getIsAvailable());
        assertFalse(responses.get(1).getIsAvailable());
        assertEquals(50L, responses.get(1).getCurrentServiceRequestId());
        assertFalse(responses.get(2).getIsActive());
        
        verify(serviceBayRepository).findAll();
    }
}