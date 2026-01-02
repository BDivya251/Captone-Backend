package com.vehiclemanagement.inventory.controller;

//package com.vehiclemanagement.inventory.controller;

import com. vehiclemanagement.inventory.dto.request.CreateInventoryItemRequest;
import com.vehiclemanagement.inventory. dto.request.UpdateInventoryItemRequest;
import com.vehiclemanagement.inventory.dto.response.InventoryItemResponse;
import com.vehiclemanagement.inventory.service. InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org. springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    
    @PostMapping
    public ResponseEntity<Void> createInventoryItem(
            @Valid @RequestBody CreateInventoryItemRequest request) {
       
        InventoryItemResponse response = inventoryService.createInventoryItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    
    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAllInventoryItems() {
      
        List<InventoryItemResponse> items = inventoryService.getAllInventoryItems();
        return ResponseEntity.ok(items);
    }
    
   
    @GetMapping("/{itemId}")
    public ResponseEntity<InventoryItemResponse> getInventoryItemById(@PathVariable Long itemId) {
        
        InventoryItemResponse item = inventoryService. getInventoryItemById(itemId);
        return ResponseEntity.ok(item);
    }
    
   
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItemResponse>> getLowStockItems(@RequestParam Long number) {
       
        List<InventoryItemResponse> items = inventoryService.getLowStockItems(number);
        return ResponseEntity.ok(items);
    }
    
   
    @PutMapping("/{itemId}")
    public ResponseEntity<InventoryItemResponse> updateInventoryItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateInventoryItemRequest request) {
        
        InventoryItemResponse response = inventoryService.updateInventoryItem(itemId, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/{itemId}/update-quantity")
    public ResponseEntity<InventoryItemResponse> updateQuantityPost(
            @PathVariable Long itemId,
            @RequestParam Integer change) {
      
        InventoryItemResponse response = inventoryService.updateQuantity(itemId, change);
        return ResponseEntity.ok(response);
    }
   
//    @PatchMapping("/{itemId}/quantity")
//    public ResponseEntity<InventoryItemResponse> updateQuantity(
//            @PathVariable Long itemId,
//            @RequestParam Integer change) {
//      
//        InventoryItemResponse response = inventoryService.updateQuantity(itemId, change);
//        return ResponseEntity.ok(response);
//    }
    
   
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Map<String, String>> deleteInventoryItem(@PathVariable Long itemId) {
      
        inventoryService.deleteInventoryItem(itemId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Inventory item deleted successfully");
        response.put("itemId", itemId.toString());
        
        return ResponseEntity.ok(response);
    }
}