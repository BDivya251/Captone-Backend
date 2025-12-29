package com.vehiclemanagement.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vehiclemanagement.userservice.entity.Customer;
public interface CustomerRepository extends JpaRepository<Customer,Long>{
	  Optional<Customer> findByUser_UserId(Long userId);
}
