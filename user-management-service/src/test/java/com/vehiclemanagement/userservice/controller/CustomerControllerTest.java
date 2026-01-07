package com.vehiclemanagement.userservice.controller;

import com.vehiclemanagement.userservice.dto.response.CustomerResponse;
import com. vehiclemanagement.userservice.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util. Arrays;
import java.util. List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension. class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void getAllCustomers_Success() {
        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .name("Customer")
                .build();

        when(customerService. getAllCustomers()).thenReturn(Arrays.asList(response));

        ResponseEntity<List<CustomerResponse>> result = customerController.getAllCustomers();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getCustomerById_Success() {
        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .name("Customer")
                .build();

        when(customerService.getCustomerById(1L)).thenReturn(response);

        ResponseEntity<CustomerResponse> result = customerController.getCustomerById(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("Customer", result.getBody().getName());
    }

    @Test
    void getCustomerByUserId_Success() {
        CustomerResponse response = CustomerResponse.builder()
                .userId(1L)
                .name("Customer")
                .build();

        when(customerService.getCustomerByUserId(1L)).thenReturn(response);

        ResponseEntity<CustomerResponse> result = customerController.getCustomerByUserId(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1L, result.getBody().getUserId());
    }
}