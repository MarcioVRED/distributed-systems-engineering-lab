package br.com.ecommerce.product.product_service.consumer;

import lombok.extern.slf4j.Slf4j;

import br.com.ecommerce.product.product_service.event.OrderCancelledEvent;
import br.com.ecommerce.product.product_service.service.StockCompensationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCancelledConsumer {

    private final StockCompensationService compensationService;
    private final ObjectMapper objectMapper;

    public OrderCancelledConsumer(
            StockCompensationService compensationService) {

        this.compensationService = compensationService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "order.cancelled",
            groupId = "product-compensation-group"
    )
    public void consume(String message) throws Exception {

        log.debug("Evento order.cancelled recebido: {}", message);

        OrderCancelledEvent event = objectMapper.readValue(
                message,
                OrderCancelledEvent.class
        );

        if (event.orderId() == null) {
            throw new IllegalArgumentException(
                    "Evento order.cancelled sem orderId"
            );
        }

        compensationService.compensate(event.orderId());

        log.info("Compensação processada para pedido: {}", event.orderId());
    }
}
