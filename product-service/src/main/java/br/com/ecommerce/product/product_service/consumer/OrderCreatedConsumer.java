package br.com.ecommerce.product.product_service.consumer;

import lombok.extern.slf4j.Slf4j;

import br.com.ecommerce.product.product_service.event.OrderCreatedEvent;
import br.com.ecommerce.product.product_service.exception.StockRejectedException;
import br.com.ecommerce.product.product_service.service.StockOutboxService;
import br.com.ecommerce.product.product_service.service.StockReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final StockReservationService stockReservationService;
    private final StockOutboxService outboxService;

    public OrderCreatedConsumer(StockReservationService stockReservationService,
                                StockOutboxService outboxService) {
        this.objectMapper = new ObjectMapper();
        this.stockReservationService = stockReservationService;
        this.outboxService = outboxService;
    }

    @KafkaListener(
            topics = "order.created",
            groupId = "product-stock-group"
    )
    public void consume(String message) throws Exception {

        OrderCreatedEvent event = objectMapper.readValue(
                message,
                OrderCreatedEvent.class
        );

        try {

            stockReservationService.reserve(event);

        } catch (StockRejectedException exception) {

            log.warn("Estoque rejeitado para pedido {}: {}", event.orderId(), exception.getMessage());

            outboxService.recordRejected(
                    event.orderId(),
                    exception.getMessage()
            );
        }
    }
}
