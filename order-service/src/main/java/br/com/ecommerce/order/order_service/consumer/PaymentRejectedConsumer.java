package br.com.ecommerce.order.order_service.consumer;

import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.event.PaymentRejectedEvent;
import br.com.ecommerce.order.order_service.service.OrderPaymentService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentRejectedConsumer {

    private final OrderPaymentService orderPaymentService;
    private final ObjectMapper objectMapper;

    public PaymentRejectedConsumer(
            OrderPaymentService orderPaymentService) {

        this.orderPaymentService = orderPaymentService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "payment.rejected",
            groupId = "order-payment-group"
    )
    public void consume(String message) throws Exception {

        log.info("Evento payment.rejected recebido: {}", message);

        PaymentRejectedEvent event =
                objectMapper.readValue(
                        message,
                        PaymentRejectedEvent.class
                );

        if (event.orderId() == null) {
            throw new IllegalArgumentException(
                    "Evento payment.rejected sem orderId"
            );
        }

        orderPaymentService.rejectPayment(
                event.orderId(),
                event.reason()
        );

        log.info("Pagamento rejeitado processado para pedido: {}", event.orderId());
    }
}
