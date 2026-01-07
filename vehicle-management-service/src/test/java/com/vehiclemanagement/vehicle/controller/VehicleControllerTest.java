package com.vehiclemanagement.vehicle.controller;

import com.fasterxml.jackson.databind. ObjectMapper;
import com.vehiclemanagement.vehicle.dto. request.CreateVehicleRequest;
import com.vehiclemanagement. vehicle.dto.request.UpdateVehicleRequest;
import com. vehiclemanagement.vehicle.dto.response.VehicleResponse;
import com.vehiclemanagement.vehicle.enums.VehicleStatus;
import com.vehiclemanagement. vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter. api.Test;
import org. springframework.beans.factory.annotation. Autowired;
import org.springframework.boot.test.autoconfigure. web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito. MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web. servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito. Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateVehicleRequest createRequest;
    private UpdateVehicleRequest updateRequest;
    private VehicleResponse vehicleResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateVehicleRequest();
        createRequest.setCustomerId(1L);
        createRequest.setRegistrationNumber("ABC123");
        createRequest.setMake("Toyota");
        createRequest.setModel("Camry");
        createRequest.setYearOfManufacture(2020);
        createRequest.setColor("Blue");
        createRequest.setVinNumber("VIN123");

        updateRequest = new UpdateVehicleRequest();
        updateRequest.setMake("Honda");

        vehicleResponse = VehicleResponse.builder()
                .id(1L)
                .customerId(1L)
                .registrationNumber("ABC123")
                .make("Toyota")
                .model("Camry")
                .yearOfManufacture(2020)
                .status(VehicleStatus.ACTIVE)
                .build();
    }

    @Test
    void createVehicle_Success() throws Exception {
        when(vehicleService.createVehicle(any(CreateVehicleRequest.class))).thenReturn(vehicleResponse);

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType. APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        verify(vehicleService).createVehicle(any(CreateVehicleRequest.class));
    }

    @Test
    void getAllVehicles_Success() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(Arrays.asList(vehicleResponse));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].registrationNumber").value("ABC123"));
    }

    @Test
    void getVehicleById_Success() throws Exception {
        when(vehicleService. getVehicleById(1L)).thenReturn(vehicleResponse);

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationNumber").value("ABC123"));
    }

    @Test
    void getVehiclesByCustomerId_Success() throws Exception {
        when(vehicleService. getVehiclesByCustomerId(1L)).thenReturn(Arrays.asList(vehicleResponse));

        mockMvc.perform(get("/api/vehicles/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1));
    }

    @Test
    void updateVehicle_Success() throws Exception {
        when(vehicleService.updateVehicle(eq(1L), any(UpdateVehicleRequest.class))).thenReturn(vehicleResponse);

        mockMvc.perform(put("/api/vehicles/1")
                .contentType(MediaType. APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteVehicle_Success() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Vehicle deleted successfully"));
    }

    @Test
    void updateStatusCode_Success() throws Exception {
        when(vehicleService.changeStatusOfVehicle(any(VehicleStatus.class), anyString()))
                .thenReturn(VehicleStatus.INACTIVE);

        mockMvc.perform(put("/api/vehicles/status")
                .param("status", "INACTIVE")
                .param("regsitration", "ABC123"))
                .andExpect(status().isOk());
    }
}