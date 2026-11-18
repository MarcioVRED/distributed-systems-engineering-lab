package br.com.ecommerce.order.order_service.event;

public record PaymentRejectedEvent(
        Long orderId,
        String reason
) {
}
