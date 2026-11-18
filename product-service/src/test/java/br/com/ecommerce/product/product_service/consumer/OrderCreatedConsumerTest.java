package br.com.ecommerce.product.product_service.consumer;

import br.com.ecommerce.product.product_service.service.StockReservationService;
import br.com.ecommerce.product.product_service.service.StockOutboxService;
import br.com.ecommerce.product.product_service.exception.StockRejectedException;
import br.com.ecommerce.product.product_service.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderCreatedConsumerTest {
    StockReservationService service = mock(StockReservationService.class);
    StockOutboxService publisher = mock(StockOutboxService.class);
    OrderCreatedConsumer consumer = new OrderCreatedConsumer(service, publisher);
    String msg = "{\"orderId\":5,\"customerId\":1,\"totalAmount\":100,\"items\":[{\"productId\":2,\"quantity\":1}]}";

    @Test
    void success() throws Exception {
        consumer.consume(msg);
        verify(service).reserve(argThat(e -> e.orderId() == 5L && e.items().size() == 1));
        verifyNoInteractions(publisher);
    }

    @Test
    void rejected() throws Exception {
        doThrow(new StockRejectedException("sem estoque")).when(service).reserve(any(OrderCreatedEvent.class));
        consumer.consume(msg);
        verify(publisher).recordRejected(5L, "sem estoque");
    }

    @Test
    void invalidJson() {
        assertThrows(Exception.class, () -> consumer.consume("not-json"));
    }
}
