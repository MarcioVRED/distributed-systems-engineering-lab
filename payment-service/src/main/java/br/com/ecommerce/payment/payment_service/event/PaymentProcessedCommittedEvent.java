package br.com.ecommerce.payment.payment_service.event;

import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;

public record PaymentProcessedCommittedEvent(
        Long orderId,
        PaymentStatus status,
        boolean newPayment
) {
}
