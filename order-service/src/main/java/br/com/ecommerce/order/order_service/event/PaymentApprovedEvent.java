package br.com.ecommerce.order.order_service.event;

public record PaymentApprovedEvent(
        Long orderId
) {
}
