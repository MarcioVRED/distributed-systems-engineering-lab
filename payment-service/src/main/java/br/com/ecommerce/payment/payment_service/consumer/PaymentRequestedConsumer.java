package br.com.ecommerce.payment.payment_service.consumer;

import br.com.ecommerce.payment.payment_service.event.PaymentRequestedEvent;
import br.com.ecommerce.payment.payment_service.service.PaymentService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class PaymentRequestedConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @KafkaListener(
            topics = "payment.requested",
            groupId = "payment-service-group"
    )
    public void consume(String message) throws Exception {

        log.debug("Evento payment.requested recebido: {}", message);

        PaymentRequestedEvent event =
                objectMapper.readValue(
                        message,
                        PaymentRequestedEvent.class
                );

        var payment = paymentService.process(event);

        log.info("Pagamento processado | Pedido: {} | Status: {}", payment.getOrderId(), payment.getStatus());
    }
}
