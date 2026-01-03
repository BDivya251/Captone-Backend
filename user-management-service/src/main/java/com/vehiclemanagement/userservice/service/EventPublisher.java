package com.vehiclemanagement.userservice.service;


import com.vehiclemanagement.userservice.config.RabbitMQConfig;
import com.vehiclemanagement.userservice.dto.request.RegisterCustomerRequest;
import com.vehiclemanagement.userservice.dto.response.RegisterResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework. stereotype.Service;

/**
 * Event Publisher
 * Publishes events to RabbitMQ
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * Publish user registration event
     */
    public void publishUserRegistration(RegisterResponse event) {
        log.info("📤 Publishing user registration event to RabbitMQ");
        log.info("User:  {} | Role: {} | Email: {}", event.getUserId(), "CUSTOMER", event.getEmail());
        
        try {
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig. USER_REGISTRATION_KEY,
                event
            );
            log.info("✅ User registration event published successfully");
        } catch (Exception e) {
            log.error("❌ Failed to publish user registration event:  {}", e.getMessage());
        }
    }
}