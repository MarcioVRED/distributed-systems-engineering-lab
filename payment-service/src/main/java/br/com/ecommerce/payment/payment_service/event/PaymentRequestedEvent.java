package br.com.ecommerce.payment.payment_service.event;

import java.math.BigDecimal;

public record PaymentRequestedEvent(
        Long orderId,
        BigDecimal amount
) {
}
