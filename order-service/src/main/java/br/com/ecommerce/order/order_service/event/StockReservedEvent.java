package br.com.ecommerce.order.order_service.event;

public record StockReservedEvent(
        Long orderId
) {
}
