
package br.com.ecommerce.payment.payment_service.producer;

import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;
import br.com.ecommerce.payment.payment_service.event.PaymentProcessedCommittedEvent;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PaymentProcessedCommittedListenerTest {

    @Test
    void publishesCorrectResult() {

        var port = mock(PaymentEventPort.class);

        var listener =
                new PaymentProcessedCommittedListener(port);

        listener.onPaymentProcessed(
                new PaymentProcessedCommittedEvent(
                        1L,
                        PaymentStatus.APPROVED,
                        true
                )
        );

        listener.onPaymentProcessed(
                new PaymentProcessedCommittedEvent(
                        2L,
                        PaymentStatus.REJECTED,
                        true
                )
        );

        listener.onPaymentProcessed(
                new PaymentProcessedCommittedEvent(
                        3L,
                        PaymentStatus.PENDING,
                        true
                )
        );

        verify(port).publishApproved(1L);
        verify(port).publishRejected(2L);

        verifyNoMoreInteractions(port);
    }
}
