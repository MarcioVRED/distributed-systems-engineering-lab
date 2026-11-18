package br.com.ecommerce.product.product_service.event;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        BigDecimal totalAmount,
        List<OrderItemEvent> items
) {
    public record OrderItemEvent(
            Long productId,
            Integer quantity
    ) {}
}
