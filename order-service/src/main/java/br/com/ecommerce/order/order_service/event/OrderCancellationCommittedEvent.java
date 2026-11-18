package br.com.ecommerce.order.order_service.event;

public record OrderCancellationCommittedEvent(
        Long orderId
) {
}
