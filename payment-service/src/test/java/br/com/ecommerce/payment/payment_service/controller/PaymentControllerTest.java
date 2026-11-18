package br.com.ecommerce.payment.payment_service.controller;

import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.service.PaymentQueryService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {
    @Test
    void delegatesQueries() {
        var svc = mock(PaymentQueryService.class);
        var ctrl = new PaymentController(svc);
        var p = new Payment(1L, BigDecimal.TEN);
        when(svc.findAll()).thenReturn(List.of(p));
        when(svc.findByOrderId(1L)).thenReturn(p);
        assertEquals(List.of(p), ctrl.findAll());
        assertSame(p, ctrl.findByOrderId(1L));
    }
}
