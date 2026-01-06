package com.vehiclemanagement.apigateway.listener;

import com.vehiclemanagement.apigateway.config.RabbitMQConfig;
import com.vehiclemanagement.apigateway.dto.request.UserRegistrationEvent;
import com.vehiclemanagement.apigateway.dto.response.EmailResponse;
import com.vehiclemanagement.apigateway.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * User Registration Listener
 * Listens to RabbitMQ for user registration events
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationListener {

    private final EmailService emailService;

    /**
     * Listen to user registration queue
     * Triggered when a new user registers
     */
    @RabbitListener(queues = RabbitMQConfig.USER_REGISTRATION_QUEUE)
    public void handleUserRegistration(UserRegistrationEvent event) {
       
        try {
            // Generate welcome email
            EmailResponse email = emailService.generateWelcomeEmail(event);

           
            emailService.sendEmail(email);

        } catch (Exception e) {
            log.error("❌ FAILED TO PROCESS USER REGISTRATION");
            log.error("Error: {}", e.getMessage(), e);
            // RabbitMQ will retry based on configuration
            throw e;
        }
    }
}