package br.com.ecommerce.product.product_service.event;

public record StockReservedEvent(
        Long orderId
) {
}
