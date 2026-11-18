package br.com.ecommerce.order.order_service.event;

public record StockRejectedEvent(
        Long orderId,
        String reason
) {
}
