package com.vehiclemanagement.apigateway.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email content response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResponse {
    private String to;           // Recipient email
    private String subject;      // Email subject
    private String body;         // Email body (plain text or HTML)
    private String contentType;  // "text/plain" or "text/html"
}