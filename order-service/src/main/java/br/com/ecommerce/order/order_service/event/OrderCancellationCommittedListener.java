
package br.com.ecommerce.order.order_service.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@RequiredArgsConstructor
public class OrderCancellationCommittedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCancellationCommittedListener.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onOrderCancellationCommitted(
            OrderCancellationCommittedEvent event) {

        Long orderId = event.orderId();

        if (orderId == null) {
            throw new IllegalArgumentException(
                    "Evento de cancelamento sem orderId"
            );
        }

        Map<String, Object> payload = Map.of(
                "orderId", orderId
        );

        kafkaTemplate.send(
                "order.cancelled",
                orderId.toString(),
                payload
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error(
                        "Erro ao publicar order.cancelled. Pedido: {}",
                        orderId,
                        exception
                );
            } else {
                log.info(
                        "Evento order.cancelled publicado. Pedido: {}",
                        orderId
                );
            }
        });
    }
}
