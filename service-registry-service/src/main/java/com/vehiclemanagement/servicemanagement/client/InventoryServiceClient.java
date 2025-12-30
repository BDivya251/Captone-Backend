package com.vehiclemanagement.servicemanagement.client;

import com.vehiclemanagement.servicemanagement.feign.InventoryItemResponse;
import org.springframework. cloud.openfeign.FeignClient;
import org.springframework. web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework. web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {
    
    @GetMapping("/api/inventory/{itemId}")
    InventoryItemResponse getInventoryItemById(@PathVariable("itemId") Long itemId);
    
    @PostMapping("/api/inventory/{itemId}/update-quantity")
    InventoryItemResponse updateQuantity(
            @PathVariable("itemId") Long itemId, 
            @RequestParam("change") Integer change
    );
}