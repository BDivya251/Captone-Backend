package com.vehiclemanagement.inventory.service;

import com.vehiclemanagement.inventory.dto.request. CreateInventoryItemRequest;
import com.vehiclemanagement.inventory.dto.request.UpdateInventoryItemRequest;
import com.vehiclemanagement.inventory.dto.response.InventoryItemResponse;
import com.vehiclemanagement.inventory.entity.InventoryItem;
import com.vehiclemanagement.inventory.enums.UnitType;
import com.vehiclemanagement.inventory. exception.BadRequestException;
import com.vehiclemanagement.inventory.exception.ResourceNotFoundException;
import com. vehiclemanagement.inventory.repository.InventoryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org. mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter. api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryItem item;
    private CreateInventoryItemRequest createRequest;
    private UpdateInventoryItemRequest updateRequest;

    @BeforeEach
    void setUp() {
        item = new InventoryItem();
        item.setId(1L);
        item.setPartNumber("P001");
        item.setPartName("Engine Oil");
        item.setQuantity(10);
        item.setUnitPrice(new BigDecimal("50.00"));
        item.setUnit(UnitType.KG);

        createRequest = new CreateInventoryItemRequest();
        createRequest. setPartNumber("P001");
        createRequest.setPartName("Engine Oil");
        createRequest. setQuantity(10);
        createRequest.setUnitPrice(new BigDecimal("50.00"));
        createRequest.setUnit(UnitType.KG);

        updateRequest = new UpdateInventoryItemRequest();
        updateRequest.setPartName("Updated Oil");
    }

    @Test
    void createInventoryItem_Success() {
        when(inventoryItemRepository.existsByPartNumber("P001")).thenReturn(false);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItemResponse response = inventoryService.createInventoryItem(createRequest);

        assertNotNull(response);
        assertEquals("P001", response.getPartNumber());
        verify(inventoryItemRepository).save(any(InventoryItem. class));
    }

    @Test
    void createInventoryItem_DuplicatePartNumber() {
        when(inventoryItemRepository. existsByPartNumber("P001")).thenReturn(true);

        assertThrows(BadRequestException.class, 
            () -> inventoryService. createInventoryItem(createRequest));
        
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void getAllInventoryItems_Success() {
        when(inventoryItemRepository.findAll()).thenReturn(Arrays.asList(item));

        List<InventoryItemResponse> responses = inventoryService.getAllInventoryItems();

        assertEquals(1, responses.size());
        assertEquals("Engine Oil", responses.get(0).getPartName());
    }

    @Test
    void getAllInventoryItems_EmptyList() {
        when(inventoryItemRepository.findAll()).thenReturn(Arrays.asList());

        List<InventoryItemResponse> responses = inventoryService.getAllInventoryItems();

        assertTrue(responses.isEmpty());
    }

    @Test
    void getInventoryItemById_Success() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));

        InventoryItemResponse response = inventoryService.getInventoryItemById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Engine Oil", response.getPartName());
    }

    @Test
    void getInventoryItemById_NotFound() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> inventoryService.getInventoryItemById(1L));
    }

    @Test
    void getLowStockItems_Success() {
        when(inventoryItemRepository.findLowStockItems(10L)).thenReturn(Arrays.asList(item));

        List<InventoryItemResponse> responses = inventoryService.getLowStockItems(10L);

        assertEquals(1, responses.size());
        verify(inventoryItemRepository).findLowStockItems(10L);
    }

    @Test
    void updateInventoryItem_Success() {
        when(inventoryItemRepository. findById(1L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItemResponse response = inventoryService. updateInventoryItem(1L, updateRequest);

        assertNotNull(response);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
    }

    @Test
    void updateInventoryItem_NotFound() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> inventoryService.updateInventoryItem(1L, updateRequest));
    }

    @Test
    void updateInventoryItem_PartialUpdate() {
        UpdateInventoryItemRequest partialRequest = new UpdateInventoryItemRequest();
        partialRequest. setPartName("New Name");
        
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any())).thenReturn(item);

        InventoryItemResponse response = inventoryService.updateInventoryItem(1L, partialRequest);

        assertNotNull(response);
        verify(inventoryItemRepository).save(any());
    }

    @Test
    void updateQuantity_IncreaseSuccess() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItemResponse response = inventoryService.updateQuantity(1L, 5);

        assertNotNull(response);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
    }

    @Test
    void updateQuantity_DecreaseSuccess() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem. class))).thenReturn(item);

        InventoryItemResponse response = inventoryService.updateQuantity(1L, -5);

        assertNotNull(response);
        verify(inventoryItemRepository).save(any(InventoryItem.class));
    }

    @Test
    void updateQuantity_InsufficientStock() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, 
            () -> inventoryService.updateQuantity(1L, -20));
        
        verify(inventoryItemRepository, never()).save(any());
    }

    @Test
    void updateQuantity_NotFound() {
        when(inventoryItemRepository. findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> inventoryService.updateQuantity(1L, 5));
    }

    @Test
    void deleteInventoryItem_Success() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        doNothing().when(inventoryItemRepository).deleteById(1L);

        inventoryService.deleteInventoryItem(1L);

        verify(inventoryItemRepository).deleteById(1L);
    }

    @Test
    void deleteInventoryItem_NotFound() {
        when(inventoryItemRepository. findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> inventoryService.deleteInventoryItem(1L));
        
        verify(inventoryItemRepository, never()).deleteById(any());
    }
}