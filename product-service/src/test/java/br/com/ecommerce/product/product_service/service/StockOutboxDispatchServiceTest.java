package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.OutboxEvent;
import br.com.ecommerce.product.product_service.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockOutboxDispatchServiceTest {
    OutboxEventRepository repo = mock(OutboxEventRepository.class);
    @SuppressWarnings("unchecked")
    KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
    StockOutboxDispatchService service = new StockOutboxDispatchService(repo, kafka);

    @Test
    void successMarksPublished() {
        var event = new OutboxEvent("stock.reserved", "7", "{}");
        when(repo.findPendingForUpdate(any(Pageable.class))).thenReturn(List.of(event));
        when(kafka.send("stock.reserved", "7", "{}")).thenReturn(CompletableFuture.completedFuture(null));
        service.dispatchBatch();
        assertEquals("PUBLISHED", event.getStatus());
        assertNotNull(event.getPublishedAt());
        assertEquals(0, event.getAttempts());
    }

    @Test
    void failureRemainsPending() {
        var event = new OutboxEvent("stock.rejected", "8", "{}");
        when(repo.findPendingForUpdate(any(Pageable.class))).thenReturn(List.of(event));
        when(kafka.send("stock.rejected", "8", "{}")).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka down")));
        service.dispatchBatch();
        assertEquals("PENDING", event.getStatus());
        assertEquals(1, event.getAttempts());
    }

    @Test
    void interruptedStopsBatch() {
        var event = new OutboxEvent("stock.reserved", "7", "{}");
        when(repo.findPendingForUpdate(any(Pageable.class))).thenReturn(List.of(event));
        when(kafka.send(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.failedFuture(new InterruptedException("interrupted")));
        service.dispatchBatch();
        assertEquals("PENDING", event.getStatus());
        assertEquals(1, event.getAttempts());
        assertTrue(Thread.interrupted());
    }

    @Test
    void noPendingDoesNotSend() {
        when(repo.findPendingForUpdate(any(Pageable.class))).thenReturn(List.of());
        service.dispatchBatch();
        verifyNoInteractions(kafka);
    }
}
