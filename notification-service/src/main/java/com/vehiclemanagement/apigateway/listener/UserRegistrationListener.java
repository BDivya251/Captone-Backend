package com.vehiclemanagement.apigateway.listener;


import com.vehiclemanagement.apigateway.config.RabbitMQConfig;
import com.vehiclemanagement.apigateway.dto.request.UserRegistrationEvent;
import com.vehiclemanagement.apigateway.dto.response.EmailResponse;
import com.vehiclemanagement.apigateway.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j. Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype. Component;

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
    @RabbitListener(queues = RabbitMQConfig. USER_REGISTRATION_QUEUE)
    public void handleUserRegistration(UserRegistrationEvent event) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📩 RECEIVED USER REGISTRATION EVENT");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("User ID:     {}", event.getUserId());
        log.info("Email:      {}", event.getEmail());
        log.info("Name:       {}", event.getName());
        log.info("Role:       {}", event.getRole());
        log.info("Registered: {}", event.getRegistrationDate());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            // Generate welcome email
            EmailResponse email = emailService.generateWelcomeEmail(event);
            
            log.info("✅ WELCOME EMAIL GENERATED");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("To:       {}", email.getTo());
            log.info("Subject: {}", email.getSubject());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("EMAIL BODY:");
            log.info("{}", email.getBody());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Here you can: 
            // 1. Send actual email via SMTP (Gmail, SendGrid, etc.)
            // 2. Save to database for audit trail
            // 3. Send to another queue for email delivery service
            // 4. Display in user's notification inbox
            
            // For now, just logging (email content is ready to send)
            log.info("📧 Email ready to send to:  {}", email.getTo());
            
        } catch (Exception e) {
            log.error("❌ FAILED TO PROCESS USER REGISTRATION");
            log.error("Error: {}", e.getMessage(), e);
            // RabbitMQ will retry based on configuration
            throw e;
        }
    }
}