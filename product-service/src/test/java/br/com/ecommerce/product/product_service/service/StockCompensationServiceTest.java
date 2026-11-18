package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.*;
import br.com.ecommerce.product.product_service.repository.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockCompensationServiceTest {
    ProductRepository products = mock(ProductRepository.class);
    StockReservationRepository reservations = mock(StockReservationRepository.class);
    StockCompensationService service = new StockCompensationService(products, reservations);

    @Test
    void rejectsNullAndMissing() {
        assertThrows(IllegalArgumentException.class, () -> service.compensate(null));
        assertThrows(IllegalStateException.class, () -> service.compensate(1L));
    }

    @Test
    void compensatesAndDoesNotRepeat() {
        StockReservation r = new StockReservation(1L);
        r.addItem(2L, 3);
        Product p = new Product("B", BigDecimal.ONE, 5);
        when(reservations.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(r));
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(p));
        service.compensate(1L);
        assertEquals(8, p.getStock());
        assertTrue(r.isCompensated());
        assertNotNull(r.getCompensatedAt());
        service.compensate(1L);
        assertEquals(8, p.getStock());
        verify(reservations).save(r);
    }

    @Test
    void emptyReservation() {
        StockReservation r = new StockReservation(1L);
        when(reservations.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(r));
        assertThrows(IllegalStateException.class, () -> service.compensate(1L));
    }

    @Test
    void missingProduct() {
        StockReservation r = new StockReservation(1L);
        r.addItem(3L, 2);
        when(reservations.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(r));
        assertThrows(IllegalStateException.class, () -> service.compensate(1L));
        assertFalse(r.isCompensated());
    }

    @Test
    void restoresMultipleProductsInSortedOrder() {
        StockReservation r = new StockReservation(1L);
        r.addItem(3L, 2);
        r.addItem(2L, 1);
        Product a = new Product("A", BigDecimal.ONE, 4), b = new Product("B", BigDecimal.ONE, 5);
        when(reservations.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(r));
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(a));
        when(products.findByIdForUpdate(3L)).thenReturn(Optional.of(b));
        service.compensate(1L);
        assertEquals(5, a.getStock());
        assertEquals(7, b.getStock());
        var order = inOrder(products);
        order.verify(products).findByIdForUpdate(2L);
        order.verify(products).findByIdForUpdate(3L);
    }

    @Test
    void overflow() {
        StockReservation r = new StockReservation(1L);
        r.addItem(2L, 1);
        Product p = new Product("A", BigDecimal.ONE, Integer.MAX_VALUE);
        when(reservations.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(r));
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(p));
        assertThrows(ArithmeticException.class, () -> service.compensate(1L));
        assertFalse(r.isCompensated());
    }

    @Test
    void invalidReservedQuantity() {
        StockReservation r = new StockReservation(1L);
        StockReservationItem bad = mock(StockReservationItem.class);
        when(bad.getProductId()).thenReturn(2L);
        when(bad.getQuantity()).thenReturn(0);
        r.getItems().add(bad);
        Product p = new Product("A", BigDecimal.ONE, 5);
        when(reservations.findByOrderIdForUpdate(1L)).thenReturn(Optional.of(r));
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(p));
        assertThrows(IllegalStateException.class, () -> service.compensate(1L));
        assertFalse(r.isCompensated());
    }
}
