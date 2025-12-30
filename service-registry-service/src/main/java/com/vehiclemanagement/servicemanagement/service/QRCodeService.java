package com.vehiclemanagement.servicemanagement.service;


import com.google.zxing. BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework. beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@Slf4j
public class QRCodeService {
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    public String generateQRCodeBase64(Long serviceRequestId) {
      
        try {
            String detailsUrl = getDetailsUrl(serviceRequestId);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(detailsUrl, BarcodeFormat.QR_CODE, 300, 300);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] qrCodeBytes = outputStream. toByteArray();
            String base64QRCode = Base64.getEncoder().encodeToString(qrCodeBytes);
            
            log.info("QR code generated successfully");
            return base64QRCode;
            
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    public String getDetailsUrl(Long serviceRequestId) {
        return baseUrl + "/vehicle/service-requests/" + serviceRequestId + "/public-details";
    }
}