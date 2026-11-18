package br.com.ecommerce.payment.payment_service.producer;

import br.com.ecommerce.payment.payment_service.entity.PaymentOutboxEvent;
import br.com.ecommerce.payment.payment_service.repository.PaymentOutboxRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentOutboxEventPublisherTest {
    @Test
    void storesApprovedAndRejected() {
        var repo = mock(PaymentOutboxRepository.class);
        var pub = new PaymentOutboxEventPublisher(repo);
        pub.publishApproved(1L);
        pub.publishRejected(2L);
        var captor = ArgumentCaptor.forClass(PaymentOutboxEvent.class);
        verify(repo, times(2)).save(captor.capture());
        assertEquals("payment.approved", captor.getAllValues().get(0).getTopic());
        assertEquals("payment.rejected", captor.getAllValues().get(1).getTopic());
        assertTrue(captor.getAllValues().get(1).getPayload().contains("reason"));
    }

    @Test
    void duplicateDoesNotCreateAnotherEvent() {
        var repo = mock(PaymentOutboxRepository.class);
        when(repo.existsByTopicAndEventKey("payment.approved", "1")).thenReturn(true);
        new PaymentOutboxEventPublisher(repo).publishApproved(1L);
        verify(repo, never()).save(any());
    }
}
