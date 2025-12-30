package com.vehiclemanagement.servicemanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vehiclemanagement.servicemanagement.dto.request.CreateServiceBayRequest;
import com.vehiclemanagement.servicemanagement.dto.response.ServiceBayResponse;
import com.vehiclemanagement.servicemanagement.entity.ServiceBay;
import com.vehiclemanagement.servicemanagement.exception.BadRequestException;
import com.vehiclemanagement.servicemanagement.repository.ServiceBayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceBayService {
	
	private final ServiceBayRepository serviceBayRepository;
	
	@Transactional
	public ServiceBayResponse createServiceBay(CreateServiceBayRequest request) {
		log.info("creating service bay : {}",request.getBayNumber());
		if(serviceBayRepository.existsByBayNumber(request.getBayNumber())) {
			throw new BadRequestException("Service Bay already existed");
		}
		ServiceBay bay=new ServiceBay();
		bay.setBayName(request.getBayName());
		bay.setBayNumber(request.getBayNumber());
		bay.setIsActive(true);
		bay.setIsAvailable(true);
		ServiceBay saved=serviceBayRepository.save(bay);
		return mapToResponse(saved);
	}
	private ServiceBayResponse mapToResponse(ServiceBay bay) {
		return ServiceBayResponse.builder()
                .id(bay.getId())
                .bayNumber(bay. getBayNumber())
                .bayName(bay.getBayName())
                .isAvailable(bay.getIsAvailable())
                .currentServiceRequestId(bay. getCurrentServiceRequestId())
                .isActive(bay.getIsActive())
                .build();
	}
}
