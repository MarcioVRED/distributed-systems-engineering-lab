package br.com.ecommerce.order.order_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.event.PaymentRequestCommittedEvent;
import br.com.ecommerce.order.order_service.repository.OrderRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
public class PaymentRequestCommittedListener {

    private final OrderRepository orderRepository;
    private final PaymentRequestedPort paymentEventPublisher;


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onPaymentRequestCommitted(
            PaymentRequestCommittedEvent event) {

        log.info("AFTER_COMMIT executado para pedido: {}", event.orderId());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Pedido não encontrado: " + event.orderId()
                        )
                );

        paymentEventPublisher.publishPaymentRequested(
                order.getId(),
                order.getTotalAmount()
        );
    }
}
