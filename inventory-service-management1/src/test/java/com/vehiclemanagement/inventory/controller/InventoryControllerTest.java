package com.vehiclemanagement.inventory.controller;

import com.vehiclemanagement.inventory. dto.request. CreateInventoryItemRequest;
import com.vehiclemanagement.inventory.dto.request.UpdateInventoryItemRequest;
import com.vehiclemanagement.inventory.dto. response.InventoryItemResponse;
import com.vehiclemanagement.inventory.enums.UnitType;
import com.vehiclemanagement.inventory.service.InventoryService;
import org.junit.jupiter.api. Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework. http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org. mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void createInventoryItem_Success() {
        CreateInventoryItemRequest request = new CreateInventoryItemRequest();
        request.setPartNumber("P001");
        
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        
        when(inventoryService.createInventoryItem(any())).thenReturn(response);
        
        ResponseEntity<Void> result = inventoryController. createInventoryItem(request);
        
        assertEquals(201, result.getStatusCode().value());
        verify(inventoryService).createInventoryItem(request);
    }

    @Test
    void getAllInventoryItems_Success() {
        InventoryItemResponse item1 = new InventoryItemResponse();
        item1.setId(1L);
        InventoryItemResponse item2 = new InventoryItemResponse();
        item2.setId(2L);
        
        when(inventoryService.getAllInventoryItems()).thenReturn(Arrays.asList(item1, item2));
        
        ResponseEntity<List<InventoryItemResponse>> result = inventoryController.getAllInventoryItems();
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void getAllInventoryItems_EmptyList() {
        when(inventoryService.getAllInventoryItems()).thenReturn(Arrays.asList());
        
        ResponseEntity<List<InventoryItemResponse>> result = inventoryController.getAllInventoryItems();
        
        assertEquals(200, result. getStatusCode().value());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void getInventoryItemById_Success() {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        response.setPartNumber("P001");
        
        when(inventoryService.getInventoryItemById(1L)).thenReturn(response);
        
        ResponseEntity<InventoryItemResponse> result = inventoryController.getInventoryItemById(1L);
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals("P001", result.getBody().getPartNumber());
    }

    @Test
    void getLowStockItems_Success() {
        InventoryItemResponse item = new InventoryItemResponse();
        item.setQuantity(5);
        
        when(inventoryService.getLowStockItems(10L)).thenReturn(Arrays.asList(item));
        
        ResponseEntity<List<InventoryItemResponse>> result = inventoryController.getLowStockItems(10L);
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result. getBody().size());
        assertEquals(5, result.getBody().get(0).getQuantity());
    }

    @Test
    void updateInventoryItem_Success() {
        UpdateInventoryItemRequest request = new UpdateInventoryItemRequest();
        request.setPartName("Updated Part");
        
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(1L);
        response.setPartName("Updated Part");
        
        when(inventoryService.updateInventoryItem(1L, request)).thenReturn(response);
        
        ResponseEntity<InventoryItemResponse> result = inventoryController.updateInventoryItem(1L, request);
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals("Updated Part", result.getBody().getPartName());
    }

    @Test
    void updateQuantityPost_IncreaseQuantity() {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setQuantity(15);
        
        when(inventoryService.updateQuantity(1L, 5)).thenReturn(response);
        
        ResponseEntity<InventoryItemResponse> result = inventoryController.updateQuantityPost(1L, 5);
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals(15, result.getBody().getQuantity());
    }

    @Test
    void updateQuantityPost_DecreaseQuantity() {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setQuantity(5);
        
        when(inventoryService.updateQuantity(1L, -5)).thenReturn(response);
        
        ResponseEntity<InventoryItemResponse> result = inventoryController.updateQuantityPost(1L, -5);
        
        assertEquals(200, result.getStatusCode().value());
        assertEquals(5, result. getBody().getQuantity());
    }

    @Test
    void deleteInventoryItem_Success() {
        doNothing().when(inventoryService).deleteInventoryItem(1L);
        
        ResponseEntity<Map<String, String>> result = inventoryController.deleteInventoryItem(1L);
        
        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().containsKey("message"));
        assertEquals("1", result.getBody().get("itemId"));
        verify(inventoryService).deleteInventoryItem(1L);
    }
}