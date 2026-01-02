package com.vehiclemanagement.apigateway.service;


import com.vehiclemanagement.apigateway.dto.request.BillNotificationRequest;
import com.vehiclemanagement.apigateway.dto.request.BookingConfirmationRequest;
import com.vehiclemanagement.apigateway.dto.response.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework. stereotype.Service;

import java.text.NumberFormat;
import java.util. Locale;

/**
 * Notification Service
 * Generates email content for various notifications
 */
@Service
@Slf4j
public class NotificationService {
    
    /**
     * Generate booking confirmation email
     */
    public EmailResponse generateBookingConfirmation(BookingConfirmationRequest request) {
        log.info("Generating booking confirmation email for:  {}", request.getCustomerEmail());
        
        String subject = "Service Booking Confirmation - Request #" + request.getServiceRequestId();
        
        String body = String.format(
            "Dear %s,\n\n" +
            "Thank you for choosing our Vehicle Service Center!\n\n" +
            "Your service request has been successfully received.\n\n" +
            "BOOKING DETAILS:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Request ID:       #%d\n" +
            "Request Type:    %s\n" +
            "Vehicle:          %s\n" +
            "Request Date:    %s\n" +
            "Description:     %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "WHAT'S NEXT?\n" +
            "• Our team will review your request shortly\n" +
            "• A manager will be assigned within 24 hours\n" +
            "• You will receive updates via email\n" +
            "• Track your request status in your dashboard\n\n" +
            "CURRENT STATUS:  PENDING\n\n" +
            "If you have any questions, please contact us.\n\n" +
            "Thank you for trusting us with your vehicle!\n\n" +
            "Best regards,\n" +
            "Vehicle Service Center Team\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            
            request.getCustomerName(),
            request.getServiceRequestId(),
            request.getRequestType(),
            request.getVehicleInfo(),
            request. getRequestDate(),
            request. getDescription()
        );
        
        return EmailResponse.builder()
                .to(request.getCustomerEmail())
                .subject(subject)
                .body(body)
                .contentType("text/plain")
                .build();
    }
    
    /**
     * Generate bill notification email
     */
    public EmailResponse generateBillNotification(BillNotificationRequest request) {
        log.info("Generating bill notification email for: {}", request.getCustomerEmail());
        
        String subject = "Service Completed - Bill #" + request.getBillNumber();
        
        // Format currency
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        
        // Build parts list
        StringBuilder partsSection = new StringBuilder();
        if (request.getPartsUsed() != null && !request.getPartsUsed().isEmpty()) {
            partsSection.append("PARTS USED:\n");
            partsSection.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            for (BillNotificationRequest.PartUsed part : request.getPartsUsed()) {
                partsSection.append(String.format(
                    "• %-30s x%d  %s  = %s\n",
                    part.getPartName(),
                    part.getQuantity(),
                    currencyFormatter.format(part.getUnitPrice()),
                    currencyFormatter.format(part.getTotalPrice())
                ));
            }
            partsSection. append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        }
        
        String body = String.format(
            "Dear %s,\n\n" +
            "Great news! Your vehicle service has been completed.\n\n" +
            "SERVICE SUMMARY:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Bill Number:     %s\n" +
            "Request ID:      #%d\n" +
            "Vehicle:         %s\n" +
            "Completed Date:  %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "%s" +
            "COST BREAKDOWN:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Labor Cost:      %s\n" +
            "Parts Cost:      %s\n" +
            "Tax (GST):       %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "TOTAL AMOUNT:    %s\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "PAYMENT INFORMATION:\n" +
            "• Please make payment at our service center\n" +
            "• Keep this bill for warranty claims\n" +
            "• Your vehicle is ready for pickup\n\n" +
            "Thank you for choosing our service!\n" +
            "We hope to serve you again.\n\n" +
            "Best regards,\n" +
            "Vehicle Service Center Team\n\n" +
            "---\n" +
            "This is an automated email. Please do not reply.",
            
            request. getCustomerName(),
            request.getBillNumber(),
            request.getServiceRequestId(),
            request.getVehicleInfo(),
            request.getCompletedDate(),
            partsSection.toString(),
            currencyFormatter.format(request.getLaborCost()),
            currencyFormatter.format(request.getPartsCost()),
            currencyFormatter.format(request.getTax()),
            currencyFormatter.format(request.getTotalAmount())
        );
        
        return EmailResponse.builder()
                .to(request.getCustomerEmail())
                .subject(subject)
                .body(body)
                .contentType("text/plain")
                .build();
    }
}