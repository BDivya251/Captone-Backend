package com.vehiclemanagement.apigateway.controller;


import com.vehiclemanagement.apigateway.dto.request.BillNotificationRequest;
import com.vehiclemanagement.apigateway.dto.request. BookingConfirmationRequest;
import com.vehiclemanagement.apigateway.dto.response. EmailResponse;
import com.vehiclemanagement.apigateway.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j. Slf4j;
import org. springframework.http.ResponseEntity;
import org.springframework. web.bind.annotation.*;

/**
 * Notification Controller
 * Returns email content (does not send emails)
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * Generate booking confirmation email
     * POST /api/notifications/booking-confirmation
     */
    @PostMapping("/booking-confirmation")
    public ResponseEntity<EmailResponse> generateBookingConfirmation(
            @RequestBody BookingConfirmationRequest request) {
        log.info("POST /api/notifications/booking-confirmation");
        EmailResponse email = notificationService.generateBookingConfirmation(request);
        return ResponseEntity.ok(email);
    }
    
    /**
     * Generate bill notification email
     * POST /api/notifications/bill-notification
     */
    @PostMapping("/bill-notification")
    public ResponseEntity<EmailResponse> generateBillNotification(
            @RequestBody BillNotificationRequest request) {
        log.info("POST /api/notifications/bill-notification");
        EmailResponse email = notificationService.generateBillNotification(request);
        return ResponseEntity.ok(email);
    }
    
    /**
     * Health check
     * GET /api/notifications/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification Service is running!");
    }
}