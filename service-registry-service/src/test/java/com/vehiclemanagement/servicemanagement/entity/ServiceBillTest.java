package com.vehiclemanagement.servicemanagement.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter. api.Assertions.*;

class ServiceBillTest {

    @Test
    void testEntityCreation() {
        ServiceBill bill = new ServiceBill();
        bill.setId(1L);
        bill.setBillNumber("BILL-123");
        bill.setLaborCost(new BigDecimal("500"));
        bill.setPartsCost(new BigDecimal("1000"));
        bill.setTax(new BigDecimal("270"));
        bill.setTotalAmount(new BigDecimal("1770"));
        bill.setPaid(false);

        assertEquals("BILL-123", bill.getBillNumber());
        assertFalse(bill.getPaid());
    }
}