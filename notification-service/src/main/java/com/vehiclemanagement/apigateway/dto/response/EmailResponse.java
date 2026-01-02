package com.vehiclemanagement.apigateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email Response
 * Contains generated email content
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResponse {
    private String to;           // Recipient email
    private String subject;      // Email subject
    private String body;         // Email body
    private String contentType;  // "text/plain" or "text/html"
    private boolean sent;        // Whether email was sent
}
