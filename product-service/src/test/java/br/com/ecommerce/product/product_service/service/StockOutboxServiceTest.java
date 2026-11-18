package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.OutboxEvent;
import br.com.ecommerce.product.product_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockOutboxServiceTest {
    OutboxEventRepository repository = mock(OutboxEventRepository.class);
    StockOutboxService service = new StockOutboxService(repository, new ObjectMapper());

    @Test
    void reservedPersistsJson() {
        service.recordReserved(7L);
        var captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(repository).save(captor.capture());
        var e = captor.getValue();
        assertEquals("stock.reserved", e.getTopic());
        assertEquals("7", e.getEventKey());
        assertTrue(e.getPayload().contains("\"orderId\":7"));
        assertEquals("PENDING", e.getStatus());
    }

    @Test
    void rejectedPersistsJson() {
        service.recordRejected(9L, "sem estoque");
        var captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(repository).save(captor.capture());
        assertEquals("stock.rejected", captor.getValue().getTopic());
        assertTrue(captor.getValue().getPayload().contains("sem estoque"));
    }

    @Test
    void duplicateDoesNotWrite() {
        when(repository.existsByTopicAndEventKey("stock.reserved", "7")).thenReturn(true);
        service.recordReserved(7L);
        verify(repository, never()).save(any());
    }

    @Test
    void rejectsNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.recordReserved(null));
    }
}
