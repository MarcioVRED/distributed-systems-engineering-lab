package br.com.ecommerce.payment.payment_service.service;

import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentQueryServiceTest {
    @Test
    void findsAllAndByOrder() {
        var repo = mock(PaymentRepository.class);
        var svc = new PaymentQueryService(repo);
        var payment = new Payment(1L, BigDecimal.TEN);
        when(repo.findAll()).thenReturn(List.of(payment));
        when(repo.findByOrderId(1L)).thenReturn(Optional.of(payment));
        assertEquals(List.of(payment), svc.findAll());
        assertSame(payment, svc.findByOrderId(1L));
    }

    @Test
    void missingPaymentReturns404() {
        var repo = mock(PaymentRepository.class);
        var svc = new PaymentQueryService(repo);
        when(repo.findByOrderId(99L)).thenReturn(Optional.empty());
        var error = assertThrows(ResponseStatusException.class, () -> svc.findByOrderId(99L));
        assertEquals(404, error.getStatusCode().value());
    }
}
