
package br.com.ecommerce.payment.payment_service.metrics;

import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;
import br.com.ecommerce.payment.payment_service.event.PaymentProcessedCommittedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentMetricsListener {

    private final PaymentMetrics paymentMetrics;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onPaymentProcessed(
            PaymentProcessedCommittedEvent event) {

        // Não contabilizar reprocessamentos
        if (!event.newPayment()) {
            return;
        }

        if (event.status() == PaymentStatus.APPROVED) {

            paymentMetrics.approved();

        } else if (event.status() == PaymentStatus.REJECTED) {

            paymentMetrics.rejected();
        }
    }
}
