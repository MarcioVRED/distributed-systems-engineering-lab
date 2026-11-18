package br.com.ecommerce.payment.payment_service.producer;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class PaymentEventPublisherTest {
    @SuppressWarnings("unchecked")
    @Test
    void publishesApprovedAndRejected() {
        KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
        when(kafka.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
        var pub = new PaymentEventPublisher(kafka);
        pub.publishApproved(1L);
        pub.publishRejected(2L);
        verify(kafka).send("payment.approved", "1", Map.of("orderId", 1L));
        verify(kafka).send("payment.rejected", "2", Map.of("orderId", 2L, "reason", "Pagamento rejeitado pela regra simulada"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void handlesAsyncFailures() {
        KafkaTemplate<String, Object> kafka = mock(KafkaTemplate.class);
        var failure = new CompletableFuture<SendResult<String, Object>>();
        failure.completeExceptionally(new RuntimeException("Kafka unavailable"));
        when(kafka.send(anyString(), anyString(), any())).thenReturn(failure);
        var pub = new PaymentEventPublisher(kafka);
        pub.publishApproved(1L);
        pub.publishRejected(2L);
        verify(kafka, times(2)).send(anyString(), anyString(), any());
    }
}
