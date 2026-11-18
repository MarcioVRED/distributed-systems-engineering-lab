package br.com.ecommerce.payment.payment_service.entity;

import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    @Test
    void initializesAndApproves() {
        Payment p = new Payment(3L, new BigDecimal("500"));
        assertEquals(3L, p.getOrderId());
        assertEquals(new BigDecimal("500"), p.getAmount());
        assertEquals(PaymentStatus.PENDING, p.getStatus());
        assertNotNull(p.getCreatedAt());
        p.approve();
        assertEquals(PaymentStatus.APPROVED, p.getStatus());
    }

    @Test
    void rejects() {
        Payment p = new Payment(4L, BigDecimal.TEN);
        p.reject();
        assertEquals(PaymentStatus.REJECTED, p.getStatus());
    }
}
