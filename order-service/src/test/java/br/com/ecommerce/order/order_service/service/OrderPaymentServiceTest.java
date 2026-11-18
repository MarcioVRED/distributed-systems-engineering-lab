
package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import br.com.ecommerce.order.order_service.metrics.OrderMetrics;
import br.com.ecommerce.order.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderPaymentServiceTest {

    OrderRepository repo = mock(OrderRepository.class);
    OrderOutboxPort pub = mock(OrderOutboxPort.class);
    OrderMetrics metrics = mock(OrderMetrics.class);

    OrderPaymentService service =
            new OrderPaymentService(repo, pub, metrics);

    Order order(OrderStatus status) {
        Order o = new Order();
        o.setId(3L);
        o.setStatus(status);

        when(repo.findById(3L))
                .thenReturn(Optional.of(o));

        return o;
    }

    @Test
    void approvesReserved() {
        Order o = order(OrderStatus.STOCK_RESERVED);

        service.approvePayment(3L);

        assertEquals(OrderStatus.PAYMENT_APPROVED, o.getStatus());
        verify(repo).save(o);
        verifyNoInteractions(metrics);
    }

    @Test
    void ignoresDuplicateApproved() {
        order(OrderStatus.PAYMENT_APPROVED);

        service.approvePayment(3L);

        verify(repo, never()).save(any());
        verifyNoInteractions(metrics);
    }

    @Test
    void ignoresConfirmed() {
        order(OrderStatus.CONFIRMED);

        service.approvePayment(3L);

        verify(repo, never()).save(any());
        verifyNoInteractions(metrics);
    }

    @Test
    void ignoresEarlyApproval() {
        order(OrderStatus.PENDING);

        service.approvePayment(3L);

        verify(repo, never()).save(any());
        verifyNoInteractions(metrics);
    }

    @Test
    void rejectsAndEmitsCompensation() {
        Order o = order(OrderStatus.STOCK_RESERVED);

        service.rejectPayment(3L, "denied");

        assertEquals(OrderStatus.CANCELLED, o.getStatus());

        verify(repo).save(o);
        verify(pub).orderCancelled(3L);

        // Verifica o registro da métrica de cancelamento.
        verify(metrics, times(1)).orderCancelled();
    }

    @Test
    void ignoresDuplicateRejection() {
        order(OrderStatus.CANCELLED);

        service.rejectPayment(3L, "x");

        verify(repo, never()).save(any());
        verifyNoInteractions(pub, metrics);
    }

    @Test
    void ignoresLateRejection() {
        order(OrderStatus.PAYMENT_APPROVED);

        service.rejectPayment(3L, "x");

        verify(repo, never()).save(any());
        verifyNoInteractions(pub, metrics);
    }

    @Test
    void missingOrder() {
        assertThrows(
                IllegalStateException.class,
                () -> service.approvePayment(3L)
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.rejectPayment(3L, "x")
        );

        verifyNoInteractions(pub, metrics);
    }
}