package com.vehiclemanagement.inventory.service;

//package com.vehiclemanagement.inventoryservice.service;

import com.vehiclemanagement.inventory.dto. request.CreateInventoryItemRequest;
import com.vehiclemanagement.inventory.dto.request.UpdateInventoryItemRequest;
import com.vehiclemanagement. inventory.dto.response. InventoryItemResponse;
import com.vehiclemanagement.inventory.entity.InventoryItem;
import com.vehiclemanagement.inventory.exception.BadRequestException;
import com.vehiclemanagement.inventory. exception.ResourceNotFoundException;
import com.vehiclemanagement.inventory.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework. stereotype.Service;
import org. springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryItemRepository inventoryItemRepository;
    
    /**
     * Create new inventory item
     */
    @Transactional
    public InventoryItemResponse createInventoryItem(CreateInventoryItemRequest request) {
        log.info("Creating inventory item:  {}", request.getPartNumber());
        
      
        if (inventoryItemRepository.existsByPartNumber(request.getPartNumber())) {
            throw new BadRequestException("Inventory item with part number " + 
                                         request.getPartNumber() + " already exists");
        }
        
        InventoryItem item = new InventoryItem();
        item.setPartNumber(request.getPartNumber());
        item.setPartName(request.getPartName());
        item.setDescription(request. getDescription());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setUnit(request.getUnit());
        
        InventoryItem savedItem = inventoryItemRepository.save(item);
        
        log.info("Inventory item created successfully with ID: {}", savedItem.getId());
        
        return mapToResponse(savedItem);
    }
    
   
    public List<InventoryItemResponse> getAllInventoryItems() {
        log.info("Fetching all inventory items");
        
        List<InventoryItem> items = inventoryItemRepository.findAll();
        List<InventoryItemResponse> responseList = new ArrayList<>();
        
        for (InventoryItem item : items) {
            responseList.add(mapToResponse(item));
        }
        
        log.info("Found {} inventory items", responseList.size());
        return responseList;
    }
   
    public InventoryItemResponse getInventoryItemById(Long itemId) {
        log.info("Fetching inventory item by ID: {}", itemId);
        
        Optional<InventoryItem> itemOptional = inventoryItemRepository. findById(itemId);
        
        if (!itemOptional. isPresent()) {
            throw new ResourceNotFoundException("Inventory item not found with ID: " + itemId);
        }
        
        return mapToResponse(itemOptional. get());
    }
    
    
    public List<InventoryItemResponse> getLowStockItems(Long number) {
        log.info("Fetching low stock items (quantity < {})",number);
        
        List<InventoryItem> items = inventoryItemRepository.findLowStockItems(number);
        List<InventoryItemResponse> responseList = new ArrayList<>();
        for (InventoryItem item : items) {
            responseList.add(mapToResponse(item));
        }
        log.info("Found {} low stock items", responseList.size());
        return responseList;
    }
    
    
    @Transactional
    public InventoryItemResponse updateInventoryItem(Long itemId, UpdateInventoryItemRequest request) {
        log.info("Updating inventory item ID: {}", itemId);
        
        Optional<InventoryItem> itemOptional = inventoryItemRepository.findById(itemId);
        
        if (!itemOptional.isPresent()) {
            throw new ResourceNotFoundException("Inventory item not found with ID: " + itemId);
        }
        
        InventoryItem item = itemOptional.get();
        
        // Update fields if provided
        if (request.getPartName() != null) {
            item.setPartName(request.getPartName());
        }
        
        if (request.getDescription() != null) {
            item. setDescription(request.getDescription());
        }
        
        if (request.getQuantity() != null) {
            item.setQuantity(request.getQuantity());
        }
        
        if (request.getUnitPrice() != null) {
            item.setUnitPrice(request. getUnitPrice());
        }
        
        if (request. getUnit() != null) {
            item.setUnit(request.getUnit());
        }
        
        InventoryItem updatedItem = inventoryItemRepository.save(item);
        
        log.info("Inventory item updated successfully:  {}", itemId);
        
        return mapToResponse(updatedItem);
    }
    
   
    @Transactional
    public InventoryItemResponse updateQuantity(Long itemId, Integer quantityChange) {
        Optional<InventoryItem> itemOptional = inventoryItemRepository.findById(itemId);
        
        if (!itemOptional.isPresent()) {
            throw new ResourceNotFoundException("Inventory item not found with ID: " + itemId);
        }
        
        InventoryItem item = itemOptional. get();
        
        int newQuantity = item.getQuantity() + quantityChange;
        
        if (newQuantity < 0) {
            throw new BadRequestException("Insufficient stock.  Current:  " + item.getQuantity() + 
                                         ", Requested:  " + Math.abs(quantityChange));
        }
        
        item.setQuantity(newQuantity);
        InventoryItem updatedItem = inventoryItemRepository.save(item);
        
        log.info("Quantity updated.  Old: {}, New: {}", item.getQuantity(), newQuantity);
        
        return mapToResponse(updatedItem);
    }
    
    
    @Transactional
    public void deleteInventoryItem(Long itemId) {
        
        Optional<InventoryItem> itemOptional = inventoryItemRepository.findById(itemId);
        
        if (!itemOptional.isPresent()) {
            throw new ResourceNotFoundException("Inventory item not found with ID: " + itemId);
        }
        
        inventoryItemRepository.deleteById(itemId);
        
        log. info("Inventory item deleted successfully:  {}", itemId);
    }
    
    /**
     * Map entity to response DTO
     */
    private InventoryItemResponse mapToResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .id(item.getId())
                .partNumber(item.getPartNumber())
                .partName(item.getPartName())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .unit(item.getUnit())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}