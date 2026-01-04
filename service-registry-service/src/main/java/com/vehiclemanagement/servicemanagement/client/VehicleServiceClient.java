package com.vehiclemanagement.servicemanagement.client;

import com.vehiclemanagement.servicemanagement.feign.VehicleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "vehicle-service")
public interface VehicleServiceClient {

    @GetMapping("/api/vehicles/{vehicleId}")
    VehicleResponse getVehicleById(@PathVariable("vehicleId") Long vehicleId);

    @PutMapping("/api/vehicles/status")
    String updateVehicleStatus(@RequestParam("status") String status,
            @RequestParam("regsitration") String registration);
}