package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class OrderOutboxAdapterTest {
    @Test
    void writesAllThreeEventsAsJson() {
        OutboxService outbox = mock(OutboxService.class);
        OrderOutboxAdapter adapter = new OrderOutboxAdapter(outbox);
        Order order = new Order();
        order.setId(12L);
        order.setCustomerId(2L);
        order.setTotalAmount(new BigDecimal("120.00"));
        OrderItem item = new OrderItem();
        item.setProductId(7L);
        item.setQuantity(3);
        order.addItem(item);
        adapter.orderCreated(order);
        adapter.paymentRequested(order);
        adapter.orderCancelled(12L);
        verify(outbox).saveEvent(eq("order.created"), eq("12"), argThat(s -> s.contains("\"productId\":7")));
        verify(outbox).saveEvent(eq("payment.requested"), eq("12"), argThat(s -> s.contains("\"orderId\":12")));
        verify(outbox).saveEvent("order.cancelled", "12", "{\"orderId\":12}");
    }
}
