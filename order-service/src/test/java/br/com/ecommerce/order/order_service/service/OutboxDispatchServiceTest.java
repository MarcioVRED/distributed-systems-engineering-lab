package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.OutboxEvent;
import br.com.ecommerce.order.order_service.entity.enums.OutboxStatus;
import br.com.ecommerce.order.order_service.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class OutboxDispatchServiceTest {
    @Test
    void publishesAndMarksPublished() {
        var repo = mock(OutboxEventRepository.class);
        KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
        var event = new OutboxEvent("order.created", "1", "{}");
        when(repo.findEventsByStatus(eq(OutboxStatus.PENDING), any(Pageable.class))).thenReturn(List.of(event));
        when(kafka.send("order.created", "1", "{}")).thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        new OutboxDispatchService(repo, kafka).dispatchBatch();
        assertEquals(OutboxStatus.PUBLISHED, event.getStatus());
    }

    @Test
    void retainsPendingAfterFailure() {
        var repo = mock(OutboxEventRepository.class);
        KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
        var event = new OutboxEvent("order.created", "1", "{}");
        when(repo.findEventsByStatus(eq(OutboxStatus.PENDING), any(Pageable.class))).thenReturn(List.of(event));
        when(kafka.send("order.created", "1", "{}")).thenReturn(CompletableFuture.failedFuture(new RuntimeException("down")));
        new OutboxDispatchService(repo, kafka).dispatchBatch();
        assertEquals(OutboxStatus.PENDING, event.getStatus());
        assertEquals(1, event.getAttempts());
    }

    @Test
    void emptyBatch() {
        var repo = mock(OutboxEventRepository.class);
        KafkaTemplate<String, String> kafka = mock(KafkaTemplate.class);
        when(repo.findEventsByStatus(eq(OutboxStatus.PENDING), any(Pageable.class))).thenReturn(List.of());
        new OutboxDispatchService(repo, kafka).dispatchBatch();
        verifyNoInteractions(kafka);
    }
}
