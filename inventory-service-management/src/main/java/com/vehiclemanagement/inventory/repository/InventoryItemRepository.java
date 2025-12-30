package com.vehiclemanagement.inventory.repository;

import com.vehiclemanagement.inventory.entity.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem,Long>{
Optional<InventoryItem> findByPartNumber(String partNumber);
    
    boolean existsByPartNumber(String partNumber);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.quantity < 10")
    List<InventoryItem> findLowStockItems();
}
