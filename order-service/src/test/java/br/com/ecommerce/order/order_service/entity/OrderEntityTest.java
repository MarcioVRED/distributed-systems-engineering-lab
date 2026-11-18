package br.com.ecommerce.order.order_service.entity;

import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {
    @Test
    void initializesDefaults() {
        Order o = new Order();
        o.prePersist();
        assertNotNull(o.getCreatedAt());
        assertEquals(OrderStatus.PENDING, o.getStatus());
    }

    @Test
    void preservesExistingValues() {
        Order o = new Order();
        LocalDateTime now = LocalDateTime.of(2020, 1, 1, 0, 0);
        o.setCreatedAt(now);
        o.setStatus(OrderStatus.CANCELLED);
        o.prePersist();
        assertEquals(now, o.getCreatedAt());
        assertEquals(OrderStatus.CANCELLED, o.getStatus());
    }

    @Test
    void maintainsBidirectionalItems() {
        Order o = new Order();
        OrderItem i = new OrderItem();
        o.addItem(i);
        assertSame(o, i.getOrder());
        assertEquals(1, o.getItems().size());
        o.removeItem(i);
        assertNull(i.getOrder());
        assertTrue(o.getItems().isEmpty());
    }
}
