package br.com.ecommerce.payment.payment_service.producer;

import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;
import br.com.ecommerce.payment.payment_service.event.PaymentProcessedCommittedEvent;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@lombok.RequiredArgsConstructor
public class PaymentProcessedCommittedListener {

    private final PaymentEventPort paymentEventPublisher;


    @TransactionalEventListener(
            phase = TransactionPhase.BEFORE_COMMIT
    )
    public void onPaymentProcessed(
            PaymentProcessedCommittedEvent event) {

        if (event.status() == PaymentStatus.APPROVED) {

            paymentEventPublisher.publishApproved(event.orderId());

        } else if (event.status() == PaymentStatus.REJECTED) {

            paymentEventPublisher.publishRejected(event.orderId());
        }
    }
}
