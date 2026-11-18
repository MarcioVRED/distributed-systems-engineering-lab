package br.com.ecommerce.product.product_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventTest {
    @Test
    void lifecycle() {
        var event = new OutboxEvent("stock.reserved", "3", "{}");
        assertNotNull(event.getId());
        assertNotNull(event.getCreatedAt());
        assertNull(event.getPublishedAt());
        assertEquals("PENDING", event.getStatus());
        event.registerFailure();
        assertEquals(1, event.getAttempts());
        event.markPublished();
        assertEquals("PUBLISHED", event.getStatus());
        assertNotNull(event.getPublishedAt());
    }
}
