package br.com.ecommerce.payment.payment_service.service;

import br.com.ecommerce.payment.payment_service.entity.PaymentOutboxEvent;
import br.com.ecommerce.payment.payment_service.repository.PaymentOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentOutboxDispatchServiceTest {
    private final PaymentOutboxRepository repository = mock(PaymentOutboxRepository.class);
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
    private final PaymentOutboxDispatchService dispatcher = new PaymentOutboxDispatchService(repository, kafka);

    private PaymentOutboxEvent event() {
        return new PaymentOutboxEvent("payment.approved", "42", "{\"orderId\":42}");
    }

    @Test
    void publishesPendingEventAndMarksPublished() {
        var event = event();
        when(repository.pendingForUpdate(any(Pageable.class))).thenReturn(List.of(event));
        when(kafka.send(eq("payment.approved"), eq("42"), any())).thenReturn(CompletableFuture.completedFuture(null));
        dispatcher.dispatchBatch();
        assertEquals("PUBLISHED", event.getStatus());
        assertEquals(0, event.getAttempts());
        verify(kafka).send(eq("payment.approved"), eq("42"), any());
    }

    @Test
    void recordsKafkaFailureWithoutMarkingPublished() {
        var event = event();
        when(repository.pendingForUpdate(any(Pageable.class))).thenReturn(List.of(event));
        var failed = new CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>>();
        failed.completeExceptionally(new IllegalStateException("Kafka unavailable"));
        when(kafka.send(eq("payment.approved"), eq("42"), any())).thenReturn(failed);
        dispatcher.dispatchBatch();
        assertEquals("PENDING", event.getStatus());
        assertEquals(1, event.getAttempts());
    }

    @Test
    void recordsInvalidPayloadAndKeepsPending() {
        var event = new PaymentOutboxEvent("payment.approved", "42", "{invalid json");
        when(repository.pendingForUpdate(any(Pageable.class))).thenReturn(List.of(event));
        dispatcher.dispatchBatch();
        assertEquals("PENDING", event.getStatus());
        assertEquals(1, event.getAttempts());
        verifyNoInteractions(kafka);
    }

    @Test
    void emptyBatchDoesNotPublish() {
        when(repository.pendingForUpdate(any(Pageable.class))).thenReturn(List.of());
        dispatcher.dispatchBatch();
        verifyNoInteractions(kafka);
    }

    @Test
    void interruptedKafkaFutureStopsProcessingAndRestoresInterruptFlag() {
        var first = event();
        var second = new PaymentOutboxEvent("payment.rejected", "43", "{\"orderId\":43}");
        when(repository.pendingForUpdate(any(Pageable.class))).thenReturn(List.of(first, second));
        var interrupted = new CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>>();
        interrupted.completeExceptionally(new InterruptedException("interrupted"));
        when(kafka.send(eq("payment.approved"), eq("42"), any())).thenReturn(interrupted);
        try {
            dispatcher.dispatchBatch();
            assertTrue(Thread.currentThread().isInterrupted());
            assertEquals(1, first.getAttempts());
            assertEquals("PENDING", second.getStatus());
            verify(kafka, times(1)).send(anyString(), anyString(), any());
        } finally {
            Thread.interrupted(); // clear the flag so subsequent tests are unaffected
        }
    }
}
