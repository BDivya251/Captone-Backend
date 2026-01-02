package com.vehiclemanagement.servicemanagement.service;

import com.vehiclemanagement.servicemanagement. entity. InventoryUsage;
import com.vehiclemanagement.servicemanagement.entity.ServiceBill;
import com.vehiclemanagement.servicemanagement.entity.ServiceRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet. MimeMessage;
import lombok. RequiredArgsConstructor;
import lombok.extern.slf4j. Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail. javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf. TemplateEngine;
import org. thymeleaf.context.Context;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util. Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.company-name}")
    private String companyName;
    
    @Value("${app.support-email}")
    private String supportEmail;
    
    /**
     * Send service completion email with bill and QR code
     */
    public void sendServiceCompletionEmail(
            String toEmail,
            String customerName,
            ServiceRequest serviceRequest,
            ServiceBill bill,
            String vehicleInfo,
            String technicianName,
            List<InventoryUsage> partsUsed,
            String qrCodeBase64,
            String detailsUrl) {
        
        log.info("Sending service completion email to: {}", toEmail);
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, companyName);
            helper.setTo(toEmail);
            helper.setSubject("Service Completed - " + bill.getBillNumber());
            
            // Create Thymeleaf context
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("billNumber", bill.getBillNumber());
            context.setVariable("vehicleInfo", vehicleInfo);
            context.setVariable("bayNumber", serviceRequest.getBayNumber());
            context.setVariable("technicianName", technicianName);
            context.setVariable("completedDate", formatDate(serviceRequest.getCompletedDate()));
            context.setVariable("requestType", serviceRequest.getRequestType());
            
            // Bill details
            context.setVariable("laborCost", formatCurrency(bill.getLaborCost()));
            context.setVariable("partsCost", formatCurrency(bill.getPartsCost()));
            context.setVariable("tax", formatCurrency(bill.getTax()));
            context.setVariable("totalAmount", formatCurrency(bill.getTotalAmount()));
            
            // Parts used
            context.setVariable("partsUsed", partsUsed);
            
            // For email, use CID reference instead of data URI
            context.setVariable("qrCodeCid", "qrcode");
            context.setVariable("detailsUrl", detailsUrl);
            context.setVariable("companyName", companyName);
            context.setVariable("supportEmail", supportEmail);
            
            // Generate HTML content
            String htmlContent = templateEngine.process("service-completion-email", context);
            helper.setText(htmlContent, true);
            
            // Attach QR code as inline image
            if (qrCodeBase64 != null && !qrCodeBase64.isEmpty()) {
                try {
                    // Remove data URI prefix if present
                    String base64Data = qrCodeBase64.replaceFirst("^data:image/[^;]+;base64,", "");
                    byte[] qrCodeBytes = java.util.Base64.getDecoder().decode(base64Data);
                    
                    log.info("QR Code bytes length: {}", qrCodeBytes.length);
                    
                    // Use ByteArrayResource for inline attachment
                    org.springframework.core.io.ByteArrayResource qrResource = 
                            new org.springframework.core.io.ByteArrayResource(qrCodeBytes);
                    
                    helper.addInline("qrcode", qrResource, "image/png");
                    log.info("QR code attached successfully as inline image");
                    
                } catch (Exception qrEx) {
                    log.error("Failed to attach QR code: {}", qrEx.getMessage(), qrEx);
                }
            } else {
                log.warn("QR code Base64 is null or empty");
            }
            
            // Send email
            mailSender.send(message);
            
            log.info("Service completion email sent successfully to: {}", toEmail);
            
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            log.error("Unexpected error sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter. ofPattern("dd MMM yyyy, hh:mm a");
        return dateTime.format(formatter);
    }
    
    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "₹0.00";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return formatter.format(amount);
    }
}