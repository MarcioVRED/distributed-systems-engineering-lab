
package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import br.com.ecommerce.order.order_service.metrics.OrderMetrics;
import br.com.ecommerce.order.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderStockServiceTest {

    OrderRepository repo = mock(OrderRepository.class);
    OrderOutboxPort pub = mock(OrderOutboxPort.class);
    OrderMetrics metrics = mock(OrderMetrics.class);

    OrderStockService service = new OrderStockService(
            repo,
            pub,
            metrics
    );

    Order order(OrderStatus status) {
        Order o = new Order();
        o.setId(3L);
        o.setStatus(status);

        when(repo.findById(3L))
                .thenReturn(Optional.of(o));

        return o;
    }

    @Test
    void reservesAndRequestsPayment() {
        Order o = order(OrderStatus.PENDING);

        service.confirmStockReservation(3L);

        assertEquals(OrderStatus.STOCK_RESERVED, o.getStatus());
        verify(repo).save(o);
        verify(pub).paymentRequested(o);
        verifyNoInteractions(metrics);
    }

    @Test
    void ignoresDuplicateReservation() {
        order(OrderStatus.STOCK_RESERVED);

        service.confirmStockReservation(3L);

        verify(repo, never()).save(any());
        verifyNoInteractions(pub, metrics);
    }

    @Test
    void ignoresLateReservation() {
        order(OrderStatus.CANCELLED);

        service.confirmStockReservation(3L);

        verify(repo, never()).save(any());
        verifyNoInteractions(pub, metrics);
    }

    @Test
    void rejectsPending() {
        Order o = order(OrderStatus.PENDING);

        service.rejectStockReservation(3L, "no stock");

        assertEquals(OrderStatus.CANCELLED, o.getStatus());
        verify(repo).save(o);

        // Verifica que o cancelamento registra a métrica.
        verify(metrics, times(1)).orderCancelled();

        // A rejeição de estoque não publica
        // uma solicitação de pagamento.
        verifyNoInteractions(pub);
    }

    @Test
    void ignoresDuplicateRejection() {
        order(OrderStatus.CANCELLED);

        service.rejectStockReservation(3L, "x");

        verify(repo, never()).save(any());
        verifyNoInteractions(pub, metrics);
    }

    @Test
    void ignoresLateRejection() {
        order(OrderStatus.PAYMENT_APPROVED);

        service.rejectStockReservation(3L, "x");

        verify(repo, never()).save(any());
        verifyNoInteractions(pub, metrics);
    }

    @Test
    void missingOrder() {
        assertThrows(
                IllegalStateException.class,
                () -> service.confirmStockReservation(3L)
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.rejectStockReservation(3L, "x")
        );

        verifyNoInteractions(pub, metrics);
    }
}
