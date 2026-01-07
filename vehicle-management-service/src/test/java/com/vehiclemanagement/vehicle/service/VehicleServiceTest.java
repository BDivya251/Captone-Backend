package com.vehiclemanagement.vehicle.service;

import com.vehiclemanagement.vehicle.client.UserServiceClient;
import com.vehiclemanagement.vehicle.dto.feign.CustomerResponse;
import com.vehiclemanagement.vehicle.dto.request.CreateVehicleRequest;
import com.vehiclemanagement.vehicle.dto.request.UpdateVehicleRequest;
import com.vehiclemanagement.vehicle.dto.response.VehicleResponse;
import com.vehiclemanagement.vehicle.entity.Vehicle;
import com.vehiclemanagement.vehicle.enums.VehicleStatus;
import com.vehiclemanagement.vehicle.exception.BadRequestException;
import com.vehiclemanagement.vehicle.exception.ResourceNotFoundException;
import com.vehiclemanagement.vehicle.repository.VehicleRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension. class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private CreateVehicleRequest createRequest;
    private UpdateVehicleRequest updateRequest;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setCustomerId(1L);
        vehicle.setRegistrationNumber("ABC123");
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYearOfManufacture(2020);
        vehicle.setColor("Blue");
        vehicle.setVinNumber("VIN123");
        vehicle.setStatus(VehicleStatus. ACTIVE);

        createRequest = new CreateVehicleRequest();
        createRequest.setCustomerId(1L);
        createRequest.setRegistrationNumber("ABC123");
        createRequest.setMake("Toyota");
        createRequest.setModel("Camry");
        createRequest.setYearOfManufacture(2020);
        createRequest.setColor("Blue");
        createRequest. setVinNumber("VIN123");

        updateRequest = new UpdateVehicleRequest();
        updateRequest. setMake("Honda");
        updateRequest.setModel("Accord");
    }

    @Test
    void createVehicle_Success() {
        when(userServiceClient.getCustomerById(1L)).thenReturn(new CustomerResponse());
        when(vehicleRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(vehicleRepository.existsByVinNumber(anyString())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.createVehicle(createRequest);

        assertNotNull(response);
        assertEquals("ABC123", response.getRegistrationNumber());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_CustomerNotFound() {
        when(userServiceClient.getCustomerById(1L)).thenThrow(FeignException.NotFound.class);

        assertThrows(BadRequestException.class, () -> vehicleService.createVehicle(createRequest));
    }

    @Test
    void createVehicle_DuplicateRegistration() {
        when(userServiceClient.getCustomerById(1L)).thenReturn(new CustomerResponse());
        when(vehicleRepository.existsByRegistrationNumber("ABC123")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vehicleService.createVehicle(createRequest));
    }

    @Test
    void createVehicle_DuplicateVin() {
        when(userServiceClient.getCustomerById(1L)).thenReturn(new CustomerResponse());
        when(vehicleRepository.existsByRegistrationNumber(anyString())).thenReturn(false);
        when(vehicleRepository.existsByVinNumber("VIN123")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> vehicleService.createVehicle(createRequest));
    }

    @Test
    void getAllVehicles_Success() {
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle));

        List<VehicleResponse> responses = vehicleService.getAllVehicles();

        assertEquals(1, responses.size());
        assertEquals("ABC123", responses. get(0).getRegistrationNumber());
    }

    @Test
    void getVehicleById_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional. of(vehicle));

        VehicleResponse response = vehicleService. getVehicleById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getVehicleById_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getVehicleById(1L));
    }

    @Test
    void getVehiclesByCustomerId_Success() {
        when(userServiceClient.getCustomerById(1L)).thenReturn(new CustomerResponse());
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(vehicle));

        List<VehicleResponse> responses = vehicleService.getVehiclesByCustomerId(1L);

        assertEquals(1, responses. size());
        verify(userServiceClient).getCustomerById(1L);
    }

    @Test
    void updateVehicle_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.updateVehicle(1L, updateRequest);

        assertNotNull(response);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_NotFound() {
        when(vehicleRepository. findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.updateVehicle(1L, updateRequest));
    }

    @Test
    void deleteVehicle_Success() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).deleteById(1L);
    }

    @Test
    void deleteVehicle_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.deleteVehicle(1L));
    }

    @Test
    void changeStatusOfVehicle_Success() {
        when(vehicleRepository.findByRegistrationNumber("ABC123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleStatus status = vehicleService.changeStatusOfVehicle(VehicleStatus.INACTIVE, "ABC123");

        assertEquals(VehicleStatus.INACTIVE, status);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void changeStatusOfVehicle_NotFound() {
        when(vehicleRepository.findByRegistrationNumber("ABC123")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, 
            () -> vehicleService.changeStatusOfVehicle(VehicleStatus.INACTIVE, "ABC123"));
    }
}