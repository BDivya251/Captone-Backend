package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement. dto.request.CreateServiceBayRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceBayResponse;
import com.vehiclemanagement.servicemanagement. entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.exception.ResourceNotFoundException;
import com.vehiclemanagement. servicemanagement.repository.ServiceBayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceBayService {
    
    private final ServiceBayRepository serviceBayRepository;
    
    @Transactional
    public ServiceBayResponse createServiceBay(CreateServiceBayRequest request) {
        log.info("Creating service bay: {}", request. getBayNumber());
        
        if (serviceBayRepository.existsByBayNumber(request.getBayNumber())) {
            throw new BadRequestException("Service bay already exists with number: " + request.getBayNumber());
        }
        
        ServiceBay bay = new ServiceBay();
        bay.setBayNumber(request.getBayNumber());
        bay.setBayName(request.getBayName());
        bay.setIsAvailable(true);
        bay.setIsActive(true);
        
        ServiceBay saved = serviceBayRepository.save(bay);
        
        log. info("Service bay created successfully");
        
        return mapToResponse(saved);
    }
    
    public List<ServiceBayResponse> getAllServiceBays() {
        log.info("Fetching all service bays");
        
        List<ServiceBay> bays = serviceBayRepository.findAll();
        List<ServiceBayResponse> responseList = new ArrayList<>();
        
        for (ServiceBay bay : bays) {
            responseList.add(mapToResponse(bay));
        }
        
        return responseList;
    }
    
    public List<ServiceBayResponse> getAvailableServiceBays() {
        log.info("Fetching available service bays");
        
        List<ServiceBay> bays = serviceBayRepository.findByIsAvailableAndIsActive(true, true);
        List<ServiceBayResponse> responseList = new ArrayList<>();
        
        for (ServiceBay bay : bays) {
            responseList.add(mapToResponse(bay));
        }
        
        log.info("Found {} available bays", responseList.size());
        
        return responseList;
    }
    
    public ServiceBay getBayByNumber(String bayNumber) {
        Optional<ServiceBay> bayOptional = serviceBayRepository.findByBayNumber(bayNumber);
        
        if (!bayOptional.isPresent()) {
            throw new ResourceNotFoundException("Service bay not found:  " + bayNumber);
        }
        
        return bayOptional.get();
    }
    
    public boolean isBayAvailable(String bayNumber) {
        ServiceBay bay = getBayByNumber(bayNumber);
        return bay.getIsAvailable() && bay.getIsActive();
    }
    
    @Transactional
    public void allocateBay(String bayNumber, Long serviceRequestId) {
        log.info("Allocating bay {} to service request {}", bayNumber, serviceRequestId);
        
        ServiceBay bay = getBayByNumber(bayNumber);
        
        if (!bay.getIsAvailable()) {
            throw new BadRequestException("Bay " + bayNumber + " is already in use by service request ID: " + 
                                         bay.getCurrentServiceRequestId());
        }
        
        if (!bay.getIsActive()) {
            throw new BadRequestException("Bay " + bayNumber + " is not active");
        }
        
        bay.setIsAvailable(false);
        bay.setCurrentServiceRequestId(serviceRequestId);
        
        serviceBayRepository. save(bay);
        
        log.info("Bay allocated successfully");
    }
    
    @Transactional
    public void releaseBay(String bayNumber) {
        log.info("Releasing bay:  {}", bayNumber);
        
        ServiceBay bay = getBayByNumber(bayNumber);
        
        bay.setIsAvailable(true);
        bay.setCurrentServiceRequestId(null);
        
        serviceBayRepository.save(bay);
        
        log.info("Bay released successfully");
    }
    
    @Transactional
    public void deleteServiceBay(Long bayId) {
        log.info("Deleting service bay ID:  {}", bayId);
        
        Optional<ServiceBay> bayOptional = serviceBayRepository. findById(bayId);
        
        if (!bayOptional.isPresent()) {
            throw new ResourceNotFoundException("Service bay not found with ID: " + bayId);
        }
        
        ServiceBay bay = bayOptional.get();
        
        if (!bay.getIsAvailable()) {
            throw new BadRequestException("Cannot delete bay that is currently in use");
        }
        
        serviceBayRepository.deleteById(bayId);
        
        log.info("Service bay deleted successfully");
    }
    
    private ServiceBayResponse mapToResponse(ServiceBay bay) {
        return ServiceBayResponse.builder()
                .id(bay.getId())
                .bayNumber(bay.getBayNumber())
                .bayName(bay.getBayName())
                .isAvailable(bay.getIsAvailable())
                .currentServiceRequestId(bay.getCurrentServiceRequestId())
                .isActive(bay.getIsActive())
                .build();
    }
}