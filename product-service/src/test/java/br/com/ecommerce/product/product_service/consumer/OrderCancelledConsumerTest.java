package br.com.ecommerce.product.product_service.consumer;

import br.com.ecommerce.product.product_service.service.StockCompensationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderCancelledConsumerTest {
    StockCompensationService service = mock(StockCompensationService.class);
    OrderCancelledConsumer consumer = new OrderCancelledConsumer(service);

    @Test
    void valid() throws Exception {
        consumer.consume("{\"orderId\":5}");
        verify(service).compensate(5L);
    }

    @Test
    void missingId() {
        assertThrows(IllegalArgumentException.class, () -> consumer.consume("{}"));
        verifyNoInteractions(service);
    }

    @Test
    void malformed() {
        assertThrows(Exception.class, () -> consumer.consume("bad-json"));
        verifyNoInteractions(service);
    }
}
