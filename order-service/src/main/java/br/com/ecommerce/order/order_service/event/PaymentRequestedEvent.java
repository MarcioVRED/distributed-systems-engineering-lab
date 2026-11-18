package br.com.ecommerce.order.order_service.event;

import java.math.BigDecimal;

public record PaymentRequestedEvent(
        Long orderId,
        BigDecimal amount
) {
}
