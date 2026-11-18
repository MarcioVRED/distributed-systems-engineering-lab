package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.*;
import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import br.com.ecommerce.order.order_service.repository.OrderRepository;
import br.com.ecommerce.order.order_service.metrics.OrderMetrics;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceUnitTest {
    OrderRepository repo = mock(OrderRepository.class);
    OrderOutboxPort publisher = mock(OrderOutboxPort.class);
    OrderMetrics orderMetrics = mock(OrderMetrics.class);

    OrderService service = new OrderService(
            repo,
            new OrderItemPolicy(),
            publisher,
            orderMetrics
    );

    Order order() {
        Order o = new Order();
        o.setId(5L);
        o.setCustomerId(1L);
        OrderItem i = new OrderItem();
        i.setProductId(2L);
        i.setQuantity(2);
        i.setUnitPrice(new BigDecimal("12000.00"));
        o.addItem(i);
        return o;
    }

    @Test
    void createsAndPublishesAfterPersistence() {
        Order o = order();
        when(repo.save(o)).thenReturn(o);
        assertSame(o, service.create(o));
        assertEquals(OrderStatus.PENDING, o.getStatus());
        assertEquals(new BigDecimal("24000.00"), o.getTotalAmount());
        assertSame(o, o.getItems().getFirst().getOrder());
        verify(publisher).orderCreated(o);
    }

    @Test
    void rejectsMissingCustomer() {
        Order o = order();
        o.setCustomerId(null);
        assertThrows(ResponseStatusException.class, () -> service.create(o));
        verifyNoInteractions(repo);
    }

    @Test
    void rejectsEmptyItems() {
        Order o = order();
        o.getItems().clear();
        assertThrows(ResponseStatusException.class, () -> service.create(o));
    }

    @Test
    void rejectsInvalidItem() {
        Order o = order();
        o.getItems().getFirst().setQuantity(0);
        assertThrows(ResponseStatusException.class, () -> service.create(o));
    }

    @Test
    void lists() {
        when(repo.findAll()).thenReturn(List.of(order()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void findsById() {
        Order o = order();
        when(repo.findWithItemsById(5L)).thenReturn(Optional.of(o));
        assertSame(o, service.findById(5L));
    }

    @Test
    void missingId() {
        assertThrows(ResponseStatusException.class, () -> service.findById(7L));
    }

    @Test
    void findsCustomer() {
        when(repo.findByCustomerId(1L)).thenReturn(List.of(order()));
        assertEquals(1, service.findByCustomerId(1L).size());
    }

    @Test
    void cancelsPending() {
        Order o = order();
        o.setStatus(OrderStatus.PENDING);
        when(repo.findWithItemsById(5L)).thenReturn(Optional.of(o));
        when(repo.save(o)).thenReturn(o);
        assertEquals(OrderStatus.CANCELLED, service.cancel(5L).getStatus());
    }

    @Test
    void rejectsCancellationAfterReservation() {
        Order o = order();
        o.setStatus(OrderStatus.STOCK_RESERVED);
        when(repo.findWithItemsById(5L)).thenReturn(Optional.of(o));
        assertThrows(ResponseStatusException.class, () -> service.cancel(5L));
        verify(repo, never()).save(any());
    }
}
