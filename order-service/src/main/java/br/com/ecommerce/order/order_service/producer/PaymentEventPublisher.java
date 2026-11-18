package br.com.ecommerce.order.order_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.event.PaymentRequestedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentRequestedPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void publishPaymentRequested(
            Long orderId,
            java.math.BigDecimal amount) {

        PaymentRequestedEvent event =
                new PaymentRequestedEvent(orderId, amount);

        kafkaTemplate.send(
                "payment.requested",
                orderId.toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error("Erro ao publicar payment.requested: {}", exception.getMessage());
            } else {
                log.info("Evento payment.requested publicado: {}", orderId);
            }
        });
    }
}
