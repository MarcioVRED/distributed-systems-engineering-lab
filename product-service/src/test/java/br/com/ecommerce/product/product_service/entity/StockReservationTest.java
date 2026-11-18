package br.com.ecommerce.product.product_service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StockReservationTest {
    @Test
    void addsItemsAndCompensatesOnce() {
        StockReservation r = new StockReservation(1L);
        assertEquals(1L, r.getOrderId());
        assertNotNull(r.getCreatedAt());
        assertFalse(r.isCompensated());
        r.addItem(2L, 3);
        assertEquals(1, r.getItems().size());
        StockReservationItem item = r.getItems().get(0);
        assertSame(r, item.getReservation());
        assertEquals(2L, item.getProductId());
        assertEquals(3, item.getQuantity());
        r.compensate();
        var at = r.getCompensatedAt();
        assertTrue(r.isCompensated());
        r.compensate();
        assertEquals(at, r.getCompensatedAt());
    }

    @Test
    void rejectsInvalidItems() {
        StockReservation r = new StockReservation(1L);
        assertThrows(IllegalArgumentException.class, () -> r.addItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> r.addItem(1L, null));
        assertThrows(IllegalArgumentException.class, () -> r.addItem(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> r.addItem(1L, -1));
    }

    @Test
    void jpaConstructors() {
        assertNotNull(new StockReservation());
        assertNotNull(new StockReservationItem());
        assertNotNull(new Product());
    }
}
