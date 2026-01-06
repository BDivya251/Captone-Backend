package com.vehiclemanagement.apigateway.service;

import com.vehiclemanagement.apigateway.dto.request.UserRegistrationEvent;
import com.vehiclemanagement.apigateway.dto.response.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Email Service
 * Generates email content for different events
 */
@Service
@Slf4j
public class EmailService {

    /**
     * Generate welcome email for new user registration
     */
    public EmailResponse generateWelcomeEmail(UserRegistrationEvent event) {
        log.info("📧 Generating welcome email for:  {}", event.getEmail());

        String subject = getSubjectByRole(event.getRole());
        String body = getBodyByRole(event);

        return EmailResponse.builder()
                .to(event.getEmail())
                .subject(subject)
                .body(body)
                .contentType("text/plain")
                .sent(false)
                .build();
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send email via SMTP
     */
    public void sendEmail(EmailResponse email) {
        try {
            log.info("📤 Sending email to: {}", email.getTo());

            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email.getTo());
            message.setSubject(email.getSubject());
            message.setText(email.getBody());

            mailSender.send(message);

            log.info("✅ Email sent successfully to: {}", email.getTo());
        } catch (Exception e) {
            log.error("❌ Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Get email subject based on user role
     */
    private String getSubjectByRole(String role) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return "Welcome to Vehicle Service Center - Admin Access Granted";
            case "MANAGER":
                return "Welcome to Vehicle Service Center - Manager Account Activated";
            case "TECHNICIAN":
                return "Welcome to Vehicle Service Center - Technician Account Created";
            case "CUSTOMER":
                return "Welcome to Vehicle Service Center - Registration Successful";
            default:
                return "Welcome to Vehicle Service Center";
        }
    }

    /**
     * Get email body based on user role
     */
    private String getBodyByRole(UserRegistrationEvent event) {
        String commonFooter = "\n\nIf you have any questions, feel free to contact our support team.\n\n" +
                "Best regards,\n" +
                "Vehicle Service Center Team\n\n" +
                "---\n" +
                "This is an automated email.  Please do not reply.\n" +
                "© 2026 Vehicle Service Center. All rights reserved. ";

        switch (event.getRole().toUpperCase()) {
            case "ADMIN":
                return String.format(
                        "Dear %s,\n\n" +
                                " Congratulations! Your Administrator account has been successfully created.\n\n" +
                                "ACCOUNT DETAILS:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "User ID:          %d\n" +
                                "Email:           %s\n" +
                                "Role:            Administrator\n" +
                                "Registration:     %s\n" +
                                "Status:          Active\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                                "ADMIN CAPABILITIES:\n" +
                                " Manage all system users (Activate/Deactivate)\n" +
                                " View and assign service requests\n" +
                                " Access complete system analytics\n" +
                                " Manage inventory and service bays\n" +
                                " Generate comprehensive reports\n\n" +
                                "NEXT STEPS:\n" +
                                "1. Log in to your admin dashboard\n" +
                                "2. Review pending manager activations\n" +
                                "3. Configure system settings\n" +
                                "4. Monitor service operations\n\n" +
                                "Dashboard URL: http://localhost:4200/admin/dashboard\n" +
                                "%s",
                        event.getName(),
                        event.getUserId(),
                        event.getEmail(),
                        event.getRegistrationDate(),
                        commonFooter);

            case "MANAGER":
                return String.format(
                        "Dear %s,\n\n" +
                                "Welcome aboard! Your Manager account has been successfully created.\n\n" +
                                "ACCOUNT DETAILS:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "User ID:         %d\n" +
                                "Email:            %s\n" +
                                "Role:            Service Manager\n" +
                                "Registration:    %s\n" +
                                "Status:           Pending Admin Approval\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                                "⚠️ IMPORTANT:  Your account is currently pending activation by an administrator.\n" +
                                "You will receive another email once your account is activated.\n\n" +
                                "MANAGER RESPONSIBILITIES:\n" +
                                "Assign technicians to service requests\n" +
                                " Monitor service progress\n" +
                                " Manage team workload\n" +
                                " Approve service completions\n" +
                                " Generate team reports\n\n" +
                                "WHAT'S NEXT:\n" +
                                "• Wait for admin approval (usually within 24 hours)\n" +
                                "• Check your email for activation confirmation\n" +
                                "• Once activated, log in to start managing services\n\n" +
                                "Dashboard URL: http://localhost:4200/manager/dashboard\n" +
                                "%s",
                        event.getName(),
                        event.getUserId(),
                        event.getEmail(),
                        event.getRegistrationDate(),
                        commonFooter);

            case "TECHNICIAN":
                return String.format(
                        "Dear %s,\n\n" +
                                " Welcome to the team! Your Technician account has been successfully created.\n\n" +
                                "ACCOUNT DETAILS:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "User ID:         %d\n" +
                                "Email:           %s\n" +
                                "Role:            Service Technician\n" +
                                "Registration:    %s\n" +
                                "Status:          Pending Admin Approval\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                                " IMPORTANT: Your account is currently pending activation by an administrator.\n" +
                                "You will receive another email once your account is activated.\n\n" +
                                "TECHNICIAN RESPONSIBILITIES:\n" +
                                " Work on assigned service requests\n" +
                                " Update service progress in real-time\n" +
                                " Request and track inventory parts\n" +
                                " Complete service work orders\n" +
                                " Communicate with managers and customers\n\n" +
                                "WHAT'S NEXT:\n" +
                                "• Wait for admin approval (usually within 24 hours)\n" +
                                "• Check your email for activation confirmation\n" +
                                "• Once activated, log in to view assigned tasks\n\n" +
                                "Dashboard URL: http://localhost:4200/technician/dashboard\n" +
                                "%s",
                        event.getName(),
                        event.getUserId(),
                        event.getEmail(),
                        event.getRegistrationDate(),
                        commonFooter);

            case "CUSTOMER":
                return String.format(
                        "Dear %s,\n\n" +
                                "Welcome to Vehicle Service Center! Your account has been successfully created.\n\n"
                                +
                                "ACCOUNT DETAILS:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "User ID:         %d\n" +
                                "Email:           %s\n" +
                                "Role:            Customer\n" +
                                "Registration:    %s\n" +
                                "Status:          Active\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                                " Your account is now active!  You can start using our services immediately.\n\n" +
                                "WHAT YOU CAN DO:\n" +
                                " Register your vehicles\n" +
                                " Create service requests with photos\n" +
                                " Track service progress in real-time\n" +
                                " View service history\n" +
                                " Download bills and invoices\n" +
                                " Manage your vehicle fleet\n\n" +
                                "GET STARTED:\n" +
                                "1. Log in to your customer dashboard\n" +
                                "2. Add your vehicle details\n" +
                                "3. Create your first service request\n" +
                                "4. Upload photos of any issues\n" +
                                "5. Track service status updates\n\n" +
                                "NEED HELP?\n" +
                                "• Visit our Help Center\n" +
                                "• Contact support:  support@vehicleservice.com\n" +
                                "• Call us: +91-1234567890\n\n" +
                                "Dashboard URL:  http://localhost:4200/customer/dashboard\n" +
                                "%s",
                        event.getName(),
                        event.getUserId(),
                        event.getEmail(),
                        event.getRegistrationDate(),
                        commonFooter);

            default:
                return String.format(
                        "Dear %s,\n\n" +
                                "Welcome to Vehicle Service Center!\n\n" +
                                "Your account has been created successfully.\n" +
                                "User ID:  %d\n" +
                                "Email: %s\n" +
                                "%s",
                        event.getName(),
                        event.getUserId(),
                        event.getEmail(),
                        commonFooter);
        }
    }
}