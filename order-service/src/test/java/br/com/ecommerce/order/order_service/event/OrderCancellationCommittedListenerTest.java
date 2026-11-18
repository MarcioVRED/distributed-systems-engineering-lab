package br.com.ecommerce.order.order_service.event;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderCancellationCommittedListenerTest {
    @Test
    void publishes() {
        KafkaTemplate<String, Object> k = mock(KafkaTemplate.class);
        when(k.send(eq("order.cancelled"), eq("3"), any())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("down")));
        new OrderCancellationCommittedListener(k).onOrderCancellationCommitted(new OrderCancellationCommittedEvent(3L));
        verify(k).send(eq("order.cancelled"), eq("3"), eq(Map.of("orderId", 3L)));
    }

    @Test
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OrderCancellationCommittedListener(mock(KafkaTemplate.class)).onOrderCancellationCommitted(new OrderCancellationCommittedEvent(null)));
    }
}
