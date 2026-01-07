package com.vehiclemanagement.servicemanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito. InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org. junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PDFServiceTest {

    @InjectMocks
    private PDFService pdfService;

    @Test
    void generateInvoicePDF_CompleteData_Success() {
        Map<String, Object> data = createCompleteInvoiceData();
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
        assertTrue(pdf.length > 1000); // PDF should be substantial
        assertTrue(new String(pdf).startsWith("%PDF")); // Valid PDF header
    }

    @Test
    void generateInvoicePDF_WithParts_Success() {
        Map<String, Object> data = createCompleteInvoiceData();
        
        List<Map<String, Object>> parts = Arrays.asList(
            createPart("Oil Filter", 2, 250.0),
            createPart("Brake Pads", 4, 300.0)
        );
        data.put("partsUsed", parts);
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
        assertTrue(pdf.length > 1000);
    }
    @Test
    void generateInvoicePDF_NullBill_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", 1L);
        data.put("bill", null);
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void generateInvoicePDF_EmptyBill_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("bill", new HashMap<>());
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
    }

    @Test
    void generateInvoicePDF_NullPartsUsed_Success() {
        Map<String, Object> data = createBasicInvoiceData();
        data.put("partsUsed", null);
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
    }

    @Test
    void generateInvoicePDF_EmptyPartsList_Success() {
        Map<String, Object> data = createBasicInvoiceData();
        data.put("partsUsed", new ArrayList<>());
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
    }

    @Test
    void generateInvoicePDF_NonMapBill_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("bill", "InvalidBillData"); // Not a Map
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf); // Should handle gracefully
    }

    @Test
    void generateInvoicePDF_NonListParts_Success() {
        Map<String, Object> data = createBasicInvoiceData();
        data.put("partsUsed", "InvalidPartData"); // Not a List
        
        byte[] pdf = pdfService. generateInvoicePDF(data);
        
        assertNotNull(pdf);
    }
    @Test
    void generateInvoicePDF_VariousNumberTypes_Success() {
        Map<String, Object> data = new HashMap<>();
        
        Map<String, Object> bill = new HashMap<>();
        bill.put("laborCost", 500); // Integer
        bill.put("partsCost", 1000L); // Long
        bill.put("tax", new BigDecimal("270.50")); // BigDecimal
        bill.put("totalAmount", "1770.50"); // String number
        data.put("bill", bill);
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
    }

    @Test
    void generateInvoicePDF_InvalidNumberStrings_Success() {
        Map<String, Object> data = new HashMap<>();
        
        Map<String, Object> bill = new HashMap<>();
        bill.put("laborCost", "invalid");
        bill.put("partsCost", null);
        bill.put("tax", "");
        bill.put("totalAmount", "NaN");
        data.put("bill", bill);
        
        byte[] pdf = pdfService. generateInvoicePDF(data);
        
        assertNotNull(pdf); // Should default to 0.0
    }

    @Test
    void generateInvoicePDF_NullServiceDetails_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", null);
        data.put("requestType", null);
        data.put("status", null);
        data.put("bayNumber", null);
        data.put("completedDate", null);
        data.put("bill", createBill(100, 200, 30, 330));
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf); // Should show "-" for null values
    }

    @Test
    void generateInvoicePDF_PartWithInvalidData_Success() {
        Map<String, Object> data = createBasicInvoiceData();
        
        List<Object> parts = new ArrayList<>();
        parts.add("NotAMap"); // Invalid part entry
        parts.add(createPart("Valid Part", 1, 100.0)); // Valid part
        data.put("partsUsed", parts);
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf); // Should skip invalid parts
    }

    @Test
    void generateInvoicePDF_ZeroCosts_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("bill", createBill(0, 0, 0, 0));
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf);
    }

    @Test
    void generateInvoicePDF_NegativeValues_Success() {
        Map<String, Object> data = new HashMap<>();
        data.put("bill", createBill(-100, 500, -50, 350));
        
        byte[] pdf = pdfService.generateInvoicePDF(data);
        
        assertNotNull(pdf); // Should handle negative values
    }
    private Map<String, Object> createCompleteInvoiceData() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", 123L);
        data.put("requestType", "REPAIR");
        data.put("status", "COMPLETED");
        data.put("bayNumber", "Bay-A1");
        data.put("completedDate", "2026-01-07");
        data.put("bill", createBill(500, 1200, 306, 2006));
        return data;
    }

    private Map<String, Object> createBasicInvoiceData() {
        Map<String, Object> data = new HashMap<>();
        data.put("serviceRequestId", 1L);
        data.put("bill", createBill(100, 200, 30, 330));
        return data;
    }

    private Map<String, Object> createBill(double labor, double parts, double tax, double total) {
        Map<String, Object> bill = new HashMap<>();
        bill.put("laborCost", labor);
        bill.put("partsCost", parts);
        bill.put("tax", tax);
        bill.put("totalAmount", total);
        return bill;
    }

    private Map<String, Object> createPart(String name, int qty, double price) {
        Map<String, Object> part = new HashMap<>();
        part.put("partName", name);
        part.put("quantity", qty);
        part.put("unitPrice", price);
        return part;
    }
}