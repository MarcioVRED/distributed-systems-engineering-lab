package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.OutboxEvent;
import br.com.ecommerce.order.order_service.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OutboxServiceTest {
    @Test
    void savesValidEvent() {
        var repo = mock(OutboxEventRepository.class);
        new OutboxService(repo).saveEvent("order.created", "7", "{}");
        verify(repo).save(argThat(e -> e.getTopic().equals("order.created") && e.getEventKey().equals("7") && e.getPayload().equals("{}")));
    }

    @Test
    void rejectsInvalidFields() {
        var repo = mock(OutboxEventRepository.class);
        var service = new OutboxService(repo);
        assertThrows(IllegalArgumentException.class, () -> service.saveEvent(" ", "1", "{}"));
        assertThrows(IllegalArgumentException.class, () -> service.saveEvent("topic", " ", "{}"));
        assertThrows(IllegalArgumentException.class, () -> service.saveEvent("topic", "1", " "));
        verifyNoInteractions(repo);
    }

    @Test
    void eventState() {
        var event = new OutboxEvent("topic", "key", "{}");
        assertEquals(0, event.getAttempts());
        event.registerFailure();
        assertEquals(1, event.getAttempts());
        event.markPublished();
        assertNotNull(event.getPublishedAt());
    }
}
