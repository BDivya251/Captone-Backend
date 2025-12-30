package com.vehiclemanagement.servicemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vehiclemanagement.servicemanagement.entity.ServiceBill;

public interface ServiceBillRepository extends JpaRepository<ServiceBill,Long>{

}
