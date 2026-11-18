package br.com.ecommerce.order.order_service.consumer;

import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.event.PaymentApprovedEvent;
import br.com.ecommerce.order.order_service.service.OrderPaymentService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentApprovedConsumer {

    private final OrderPaymentService orderPaymentService;
    private final ObjectMapper objectMapper;

    public PaymentApprovedConsumer(
            OrderPaymentService orderPaymentService) {

        this.orderPaymentService = orderPaymentService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "payment.approved",
            groupId = "order-payment-group"
    )
    public void consume(String message) throws Exception {

        log.info("Evento payment.approved recebido: {}", message);

        PaymentApprovedEvent event =
                objectMapper.readValue(
                        message,
                        PaymentApprovedEvent.class
                );

        if (event.orderId() == null) {
            throw new IllegalArgumentException(
                    "Evento payment.approved sem orderId"
            );
        }

        orderPaymentService.approvePayment(
                event.orderId()
        );

        log.info("Pagamento aprovado processado para pedido: {}", event.orderId());
    }
}