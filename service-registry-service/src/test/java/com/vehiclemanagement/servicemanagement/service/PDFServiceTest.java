package com.vehiclemanagement.servicemanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org. mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PDFServiceTest {

    @InjectMocks
    private PDFService pdfService;

    @Test
    void generateInvoicePDF_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", 1L);
        data.put("requestType", "REPAIR");
        data.put("status", "COMPLETED");

        Map<String, Object> bill = new HashMap<>();
        bill.put("laborCost", 500.0);
        bill.put("partsCost", 1000.0);
        bill.put("tax", 270.0);
        bill.put("totalAmount", 1770.0);
        data.put("bill", bill);

        data.put("partsUsed", new ArrayList<>());

        byte[] pdf = pdfService.generateInvoicePDF(data);
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void generateInvoicePDF_WithNullBill() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", 1L);
        data.put("bill", null);
        data.put("partsUsed", new ArrayList<>());

        byte[] pdf = pdfService.generateInvoicePDF(data);
        assertNotNull(pdf);
    }
}