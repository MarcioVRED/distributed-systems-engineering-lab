package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemPolicyTest {
    private final OrderItemPolicy policy = new OrderItemPolicy();

    private OrderItem item(Long id, Integer quantity, BigDecimal price) {
        OrderItem i = new OrderItem();
        i.setProductId(id);
        i.setQuantity(quantity);
        i.setUnitPrice(price);
        return i;
    }

    @Test
    void computesSubtotal() {
        assertEquals(0, new BigDecimal("24.00").compareTo(policy.subtotal(item(1L, 2, new BigDecimal("12.00")))));
    }

    @Test
    void rejectsInvalid() {
        assertThrows(ResponseStatusException.class, () -> policy.validate(null));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(null, 1, BigDecimal.ONE)));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(1L, null, BigDecimal.ONE)));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(1L, 0, BigDecimal.ONE)));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(1L, -1, BigDecimal.ONE)));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(1L, 1, null)));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(1L, 1, BigDecimal.ZERO)));
        assertThrows(ResponseStatusException.class, () -> policy.validate(item(1L, 1, BigDecimal.ONE.negate())));
    }
}
