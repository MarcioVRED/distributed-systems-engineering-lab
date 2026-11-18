package br.com.ecommerce.order.order_service.producer;

import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.event.*;
import br.com.ecommerce.order.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderListenersTest {
    @Test
    void created() {
        OrderRepository r = mock(OrderRepository.class);
        OrderCreatedPort port = mock(OrderCreatedPort.class);
        Order o = new Order();
        when(r.findWithItemsById(1L)).thenReturn(Optional.of(o));
        new OrderCreatedListener(r, port).onOrderCreated(new OrderPersistedEvent(1L));
        verify(port).publish(o);
    }

    @Test
    void createdMissing() {
        OrderRepository r = mock(OrderRepository.class);
        assertThrows(Exception.class, () -> new OrderCreatedListener(r, mock(OrderCreatedPort.class)).onOrderCreated(new OrderPersistedEvent(1L)));
    }

    @Test
    void paymentRequested() {
        OrderRepository r = mock(OrderRepository.class);
        PaymentRequestedPort port = mock(PaymentRequestedPort.class);
        Order o = new Order();
        o.setId(1L);
        o.setTotalAmount(BigDecimal.TEN);
        when(r.findById(1L)).thenReturn(Optional.of(o));
        new PaymentRequestCommittedListener(r, port).onPaymentRequestCommitted(new PaymentRequestCommittedEvent(1L));
        verify(port).publishPaymentRequested(1L, BigDecimal.TEN);
    }

    @Test
    void paymentRequestMissing() {
        OrderRepository r = mock(OrderRepository.class);
        assertThrows(IllegalStateException.class, () -> new PaymentRequestCommittedListener(r, mock(PaymentRequestedPort.class)).onPaymentRequestCommitted(new PaymentRequestCommittedEvent(1L)));
    }
}
