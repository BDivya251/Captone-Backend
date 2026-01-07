package com.vehiclemanagement.userservice.service;

import com.vehiclemanagement.userservice.dto.response.CustomerResponse;
import com. vehiclemanagement.userservice.entity.Customer;
import com. vehiclemanagement.userservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org. mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setUserId(1L);
        customer.setName("Test Customer");
        customer.setPhone("1234567890");
    }

    @Test
    void getAllCustomers_Success() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer));

        List<CustomerResponse> responses = customerService.getAllCustomers();

        assertEquals(1, responses.size());
        assertEquals("Test Customer", responses.get(0).getName());
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService. getCustomerById(1L);

        assertNotNull(response);
        assertEquals("Test Customer", response.getName());
    }

    @Test
    void getCustomerByUserId_Success() {
        when(customerRepository. findByUserId(1L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getCustomerByUserId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
    }
}