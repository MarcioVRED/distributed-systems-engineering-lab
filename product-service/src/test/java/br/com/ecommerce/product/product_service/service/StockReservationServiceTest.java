package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.*;
import br.com.ecommerce.product.product_service.event.*;
import br.com.ecommerce.product.product_service.exception.StockRejectedException;
import br.com.ecommerce.product.product_service.repository.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockReservationServiceTest {
    ProductRepository products = mock(ProductRepository.class);
    StockReservationRepository reservations = mock(StockReservationRepository.class);
    StockOutboxService publisher = mock(StockOutboxService.class);
    StockReservationService service = new StockReservationService(products, reservations, publisher, new DefaultStockAvailabilityPolicy());

    OrderCreatedEvent evt(Long id, OrderCreatedEvent.OrderItemEvent... items) {
        return new OrderCreatedEvent(id, 1L, BigDecimal.TEN, items == null ? null : Arrays.asList(items));
    }

    OrderCreatedEvent.OrderItemEvent item(Long id, Integer qty) {
        return new OrderCreatedEvent.OrderItemEvent(id, qty);
    }

    @Test
    void reservesAggregatedItemsAndPublishes() {
        Product p = new Product("A", BigDecimal.TEN, 10);
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(p));
        service.reserve(evt(7L, item(2L, 2), item(2L, 3)));
        assertEquals(5, p.getStock());
        verify(reservations).save(argThat(r -> r.getOrderId() == 7L && r.getItems().size() == 1 && r.getItems().get(0).getQuantity() == 5));
        verify(publisher).recordReserved(7L);
    }

    @Test
    void alreadyReservedIsNoop() {
        when(reservations.existsByOrderId(7L)).thenReturn(true);
        service.reserve(evt(7L, item(2L, 1)));
        verifyNoInteractions(products, publisher);
        verify(reservations, never()).save(any());
    }

    @Test
    void invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> service.reserve(evt(null, item(2L, 1))));
        assertThrows(IllegalArgumentException.class, () -> service.reserve(evt(7L, (OrderCreatedEvent.OrderItemEvent[]) null)));
        assertThrows(IllegalArgumentException.class, () -> service.reserve(evt(7L)));
        assertThrows(IllegalArgumentException.class, () -> service.reserve(evt(7L, item(null, 1))));
        assertThrows(IllegalArgumentException.class, () -> service.reserve(evt(7L, item(2L, null))));
        assertThrows(IllegalArgumentException.class, () -> service.reserve(evt(7L, item(2L, 0))));
    }

    @Test
    void missingOrInsufficientStock() {
        assertThrows(StockRejectedException.class, () -> service.reserve(evt(7L, item(2L, 1))));
        Product p = new Product("A", BigDecimal.ONE, 1);
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(p));
        assertThrows(StockRejectedException.class, () -> service.reserve(evt(7L, item(2L, 2))));
        assertEquals(1, p.getStock());
        verify(reservations, never()).save(any());
    }

    @Test
    void validatesAllBeforeMutation() {
        Product a = new Product("A", BigDecimal.ONE, 5);
        Product b = new Product("B", BigDecimal.ONE, 0);
        when(products.findByIdForUpdate(1L)).thenReturn(Optional.of(a));
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(b));
        assertThrows(StockRejectedException.class, () -> service.reserve(evt(8L, item(1L, 2), item(2L, 1))));
        assertEquals(5, a.getStock());
    }

    @Test
    void overflowIsRejected() {
        assertThrows(ArithmeticException.class, () -> service.reserve(evt(8L, item(1L, Integer.MAX_VALUE), item(1L, 1))));
    }

    @Test
    void nullStockRejected() {
        Product p = new Product("A", BigDecimal.ONE, null);
        when(products.findByIdForUpdate(2L)).thenReturn(Optional.of(p));
        assertThrows(StockRejectedException.class, () -> service.reserve(evt(7L, item(2L, 1))));
    }
}
