package com.vehiclemanagement.apigateway.service;

import com.vehiclemanagement.apigateway.config.RabbitMQConfig;
import com.vehiclemanagement.apigateway.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailEvent(EmailEvent emailEvent) {
        log.info("Received email event from queue for recipient: {}", emailEvent.getTo());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailEvent.getTo());
            helper.setSubject(emailEvent.getSubject());

            // Build HTML content
            String htmlContent = buildEmailContent(emailEvent);
            helper.setText(htmlContent, true);

            // Add QR code as inline attachment if present
            if (emailEvent.getQrCodeBase64() != null && !emailEvent.getQrCodeBase64().isEmpty()) {
                String base64Data = emailEvent.getQrCodeBase64()
                        .replaceFirst("^data:image/[^;]+;base64,", "");
                byte[] qrCodeBytes = Base64.getDecoder().decode(base64Data);
                ByteArrayResource qrResource = new ByteArrayResource(qrCodeBytes);
                helper.addInline("qrcode", qrResource, "image/png");
                log.info("QR code attached successfully");
            }

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", emailEvent.getTo());

        } catch (Exception e) {
            log.error("Error sending email to: {}", emailEvent.getTo(), e);
        }
    }

    private String buildEmailContent(EmailEvent event) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2 style='color: #006666;'>Service Completed</h2>");
        html.append("<p>Dear <strong>").append(event.getCustomerName()).append("</strong>,</p>");
        html.append("<p>Your service request has been completed successfully.</p>");

        html.append("<h3 style='color: #006666;'>Service Details</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse;'>");
        html.append("<tr><td style='border: 1px solid #ddd; padding: 8px;'><strong>Bill Number:</strong></td>");
        html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(event.getBillNumber()).append("</td></tr>");
        html.append("<tr><td style='border: 1px solid #ddd; padding: 8px;'><strong>Vehicle:</strong></td>");
        html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(event.getVehicleInfo()).append("</td></tr>");
        html.append("</table>");

        html.append("<h3 style='color: #006666;'>Bill Summary</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse;'>");
        html.append("<tr><td style='border: 1px solid #ddd; padding: 8px;'><strong>Labor Cost:</strong></td>");
        html.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹ ").append(String.format("%.2f", event.getLaborCost())).append("</td></tr>");
        html.append("<tr><td style='border: 1px solid #ddd; padding: 8px;'><strong>Parts Cost:</strong></td>");
        html.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹ ").append(String.format("%.2f", event.getPartsCost())).append("</td></tr>");
        html.append("<tr><td style='border: 1px solid #ddd; padding: 8px;'><strong>Tax (18%):</strong></td>");
        html.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹ ").append(String.format("%.2f", event.getTax())).append("</td></tr>");
        html.append("<tr style='background: #f0f0f0;'><td style='border: 1px solid #ddd; padding: 8px;'><strong>Total Amount:</strong></td>");
        html.append("<td style='border: 1px solid #ddd; padding: 8px;'><strong>₹ ").append(String.format("%.2f", event.getTotalAmount())).append("</strong></td></tr>");
        html.append("</table>");

        if (event.getPartsUsed() != null && !event.getPartsUsed().isEmpty()) {
            html.append("<h3 style='color: #006666;'>Parts Used</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse;'>");
            html.append("<tr style='background: #006666; color: white;'>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Part Name</th>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Quantity</th>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Unit Price</th>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Total</th></tr>");

            for (var part : event.getPartsUsed()) {
                html.append("<tr>");
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(part.get("partName")).append("</td>");
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(part.get("quantity")).append("</td>");
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹ ").append(part.get("unitPrice")).append("</td>");
                double qty = ((Number) part.get("quantity")).doubleValue();
                double price = ((Number) part.get("unitPrice")).doubleValue();
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹ ").append(String.format("%.2f", qty * price)).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        html.append("<h3 style='color: #006666; margin-top: 20px;'>QR Code</h3>");
        html.append("<img src='cid:qrcode' alt='QR Code' style='width: 200px; height: 200px;' />");
        html.append("<p><small>Scan this QR code to download your invoice.</small></p>");

        html.append("<hr style='border: 1px solid #ddd; margin: 20px 0;'>");
        html.append("<p style='color: #666; font-size: 12px;'>");
        html.append("Vehicle Service Management System<br>");
        html.append("123 Service Road, Service City<br>");
        html.append("Phone: +91-1234-567890 | Email: support@vehicleservice.com");
        html.append("</p>");
        html.append("</body></html>");

        return html.toString();
    }
}
