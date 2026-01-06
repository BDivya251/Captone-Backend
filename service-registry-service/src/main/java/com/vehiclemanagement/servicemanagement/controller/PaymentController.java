package com.vehiclemanagement.servicemanagement.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.vehiclemanagement.servicemanagement.entity.ServiceBill;
import com.vehiclemanagement.servicemanagement.repository.ServiceBillRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vehicle/payments")
// @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true") //
// REMOVED to avoid duplicate headers with Gateway
@Slf4j
public class PaymentController {

    private final ServiceBillRepository serviceBillRepository;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public PaymentController(
            ServiceBillRepository serviceBillRepository,
            @Value("${razorpay.key.id}") String keyId,
            @Value("${razorpay.key.secret}") String keySecret) throws RazorpayException {
        this.serviceBillRepository = serviceBillRepository;
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
        log.info("Razorpay Payment Controller initialized");
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        log.info("Creating payment order for bill: {}", request.get("billId"));

        try {
            Long billId = Long.valueOf(request.get("billId").toString());

            // Get bill
            ServiceBill bill = serviceBillRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found"));

            // Check if already paid
            if (Boolean.TRUE.equals(bill.getPaid())) {
                throw new RuntimeException("Bill already paid");
            }

            // Convert to paise (₹100 = 10000 paise)
            int amountInPaise = bill.getTotalAmount()
                    .multiply(new BigDecimal(100))
                    .intValue();

            // Create Razorpay order
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "bill_" + bill.getBillNumber());
            orderRequest.put("payment_capture", 1); // Auto-capture

            Order order = razorpayClient.orders.create(orderRequest);

            // Save order ID to bill
            bill.setRazorpayOrderId(order.get("id"));
            serviceBillRepository.save(bill);

            // Response
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", amountInPaise);
            response.put("currency", "INR");
            response.put("keyId", razorpayKeyId);
            response.put("billId", billId);
            response.put("billNumber", bill.getBillNumber());
            response.put("description", "Payment for Bill " + bill.getBillNumber());

            return ResponseEntity.ok(response);

        } catch (RazorpayException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Payment order creation failed:  " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, Object> request) {
        log.info("Verifying payment for bill:  {}", request.get("billId"));

        try {
            Long billId = Long.valueOf(request.get("billId").toString());
            String razorpayPaymentId = request.get("razorpayPaymentId").toString();
            String razorpayOrderId = request.get("razorpayOrderId").toString();

            ServiceBill bill = serviceBillRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found"));

            if (!razorpayOrderId.equals(bill.getRazorpayOrderId())) {
                throw new RuntimeException("Order ID mismatch");
            }

            bill.setPaid(true);
            bill.setRazorpayPaymentId(razorpayPaymentId);
            bill.setPaymentDate(LocalDateTime.now());
            serviceBillRepository.save(bill);

            log.info("Bill #{} marked as PAID.  Payment ID: {}",
                    bill.getBillNumber(), razorpayPaymentId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment verified successfully",
                    "paymentId", razorpayPaymentId,
                    "billNumber", bill.getBillNumber()));

        } catch (Exception e) {
            log.error("Payment verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/status/{billId}")
    public ResponseEntity<Map<String, Object>> getPaymentStatus(@PathVariable Long billId) {
        log.info("Getting payment status for bill: {}", billId);

        try {
            ServiceBill bill = serviceBillRepository.findById(billId)
                    .orElseThrow(() -> new RuntimeException("Bill not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("billId", bill.getId());
            response.put("billNumber", bill.getBillNumber());
            response.put("totalAmount", bill.getTotalAmount());
            response.put("paid", bill.getPaid());
            response.put("paymentId", bill.getRazorpayPaymentId());
            response.put("paymentDate", bill.getPaymentDate());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment service running with Razorpay");
    }
}