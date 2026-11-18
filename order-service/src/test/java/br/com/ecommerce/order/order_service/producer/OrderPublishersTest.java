package br.com.ecommerce.order.order_service.producer;

import br.com.ecommerce.order.order_service.entity.*;
import br.com.ecommerce.order.order_service.event.*;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderPublishersTest {
    @Test
    void orderCreated() {
        KafkaTemplate<String, OrderCreatedEvent> k = mock(KafkaTemplate.class);
        when(k.send(eq("order.created"), eq("5"), any(OrderCreatedEvent.class))).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka down")));
        Order o = new Order();
        o.setId(5L);
        o.setCustomerId(1L);
        o.setTotalAmount(BigDecimal.TEN);
        OrderItem i = new OrderItem();
        i.setProductId(2L);
        i.setQuantity(3);
        o.addItem(i);
        new OrderEventPublisher(k).publish(o);
        verify(k).send(eq("order.created"), eq("5"), argThat(e -> e.items().size() == 1 && e.items().getFirst().quantity() == 3));
    }

    @Test
    void paymentRequested() {
        KafkaTemplate<String, Object> k = mock(KafkaTemplate.class);
        when(k.send(eq("payment.requested"), eq("5"), any())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("down")));
        new PaymentEventPublisher(k).publishPaymentRequested(5L, BigDecimal.TEN);
        verify(k).send(eq("payment.requested"), eq("5"), any(PaymentRequestedEvent.class));
    }
}
