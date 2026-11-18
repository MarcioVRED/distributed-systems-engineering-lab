package br.com.ecommerce.payment.payment_service.producer;

import org.springframework.kafka.core.KafkaTemplate;


import java.util.Map;

@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class PaymentEventPublisher implements PaymentEventPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publishApproved(Long orderId) {

        kafkaTemplate.send(
                "payment.approved",
                orderId.toString(),
                Map.of("orderId", orderId)
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error("Erro ao publicar payment.approved | orderId={}", orderId, exception);
            } else {
                log.info("Evento payment.approved publicado | orderId={}", orderId);
            }
        });
    }

    public void publishRejected(Long orderId) {

        kafkaTemplate.send(
                "payment.rejected",
                orderId.toString(),
                Map.of(
                        "orderId", orderId,
                        "reason", "Pagamento rejeitado pela regra simulada"
                )
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error("Erro ao publicar payment.rejected | orderId={}", orderId, exception);
            } else {
                log.info("Evento payment.rejected publicado | orderId={}", orderId);
            }
        });
    }
}
