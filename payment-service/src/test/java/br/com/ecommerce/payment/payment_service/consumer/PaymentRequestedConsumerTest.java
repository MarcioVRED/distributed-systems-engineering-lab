package br.com.ecommerce.payment.payment_service.consumer;

import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class PaymentRequestedConsumerTest {
    @Test
    void consumesValidJson() throws Exception {
        var service = mock(PaymentService.class);
        when(service.process(any())).thenReturn(new Payment(7L, BigDecimal.TEN));
        new PaymentRequestedConsumer(service).consume("{\"orderId\":7,\"amount\":10}");
        verify(service).process(argThat(e -> e.orderId() == 7L && e.amount().compareTo(BigDecimal.TEN) == 0));
    }

    @Test
    void rejectsInvalidJson() {
        var service = mock(PaymentService.class);
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> new PaymentRequestedConsumer(service).consume("not-json"));
        verifyNoInteractions(service);
    }
}
