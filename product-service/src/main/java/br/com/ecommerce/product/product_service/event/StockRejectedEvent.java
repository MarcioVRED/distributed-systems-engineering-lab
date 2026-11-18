package br.com.ecommerce.product.product_service.event;

public record StockRejectedEvent(
        Long orderId,
        String reason
) {
}
