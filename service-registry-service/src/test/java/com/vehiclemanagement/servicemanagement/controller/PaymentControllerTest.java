package com.vehiclemanagement.servicemanagement. controller;

import com.vehiclemanagement.servicemanagement.entity.ServiceBill;
import com.vehiclemanagement.servicemanagement.repository.ServiceBillRepository;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter. api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito. junit.jupiter.MockitoExtension;
import org.springframework. http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test. util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org. mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private ServiceBillRepository serviceBillRepository;

    private PaymentController paymentController;
    private ServiceBill testBill;

    @BeforeEach
    void setUp() throws Exception {
          paymentController = new PaymentController(
                serviceBillRepository,
                "test_key_id",
                "test_key_secret"
        );

        // Setup test bill
        testBill = new ServiceBill();
        testBill.setId(1L);
        testBill.setBillNumber("BILL-001");
        testBill.setLaborCost(new BigDecimal("500.00"));
        testBill.setPartsCost(new BigDecimal("1200.00"));
        testBill.setTax(new BigDecimal("306.00"));
        testBill.setTotalAmount(new BigDecimal("2006.00"));
        testBill.setPaid(false);
        testBill.setGeneratedDate(LocalDateTime.now());
    }
    @Test
    void getPaymentStatus_UnpaidBill_Success() {
        when(serviceBillRepository.findById(1L)).thenReturn(Optional. of(testBill));

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("billId"));
        assertEquals("BILL-001", response.getBody().get("billNumber"));
        assertEquals(new BigDecimal("2006.00"), response.getBody().get("totalAmount"));
        assertEquals(false, response.getBody().get("paid"));
        assertNull(response.getBody().get("paymentId"));

        verify(serviceBillRepository).findById(1L);
    }

    @Test
    void getPaymentStatus_PaidBill_Success() {
        testBill.setPaid(true);
        testBill.setRazorpayPaymentId("pay_test456");
        testBill.setPaymentDate(LocalDateTime.now());

        when(serviceBillRepository.findById(1L)).thenReturn(Optional.of(testBill));

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("paid"));
        assertEquals("pay_test456", response.getBody().get("paymentId"));
        assertNotNull(response.getBody().get("paymentDate"));

        verify(serviceBillRepository).findById(1L);
    }

    @Test
    void getPaymentStatus_BillNotFound() {
        when(serviceBillRepository.findById(999L)).thenReturn(Optional. empty());

        ResponseEntity<Map<String, Object>> response = paymentController.getPaymentStatus(999L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));

        verify(serviceBillRepository).findById(999L);
    }
    @Test
    void verifyPayment_Success() {
        testBill.setRazorpayOrderId("order_test123");

        Map<String, Object> request = new HashMap<>();
        request.put("billId", 1L);
        request.put("razorpayPaymentId", "pay_test456");
        request.put("razorpayOrderId", "order_test123");

        when(serviceBillRepository. findById(1L)).thenReturn(Optional.of(testBill));
        when(serviceBillRepository.save(any(ServiceBill.class))).thenReturn(testBill);

        ResponseEntity<Map<String, Object>> response = paymentController.verifyPayment(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("success"));
        assertEquals("pay_test456", response.getBody().get("paymentId"));

        assertTrue(testBill.getPaid());
        assertEquals("pay_test456", testBill. getRazorpayPaymentId());

        verify(serviceBillRepository).save(testBill);
    }

    @Test
    void verifyPayment_BillNotFound() {
        Map<String, Object> request = new HashMap<>();
        request.put("billId", 999L);
        request.put("razorpayPaymentId", "pay_test456");
        request.put("razorpayOrderId", "order_test123");

        when(serviceBillRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = paymentController.verifyPayment(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));

        verify(serviceBillRepository, never()).save(any());
    }

    @Test
    void verifyPayment_OrderIdMismatch() {
        testBill.setRazorpayOrderId("order_different");

        Map<String, Object> request = new HashMap<>();
        request.put("billId", 1L);
        request.put("razorpayPaymentId", "pay_test456");
        request.put("razorpayOrderId", "order_test123");

        when(serviceBillRepository.findById(1L)).thenReturn(Optional.of(testBill));

        ResponseEntity<Map<String, Object>> response = paymentController. verifyPayment(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(false, response.getBody().get("success"));

        verify(serviceBillRepository, never()).save(any());
    }

    @Test
    void health_ReturnsOk() {
        ResponseEntity<String> response = paymentController.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment service running with Razorpay", response.getBody());
    }
}