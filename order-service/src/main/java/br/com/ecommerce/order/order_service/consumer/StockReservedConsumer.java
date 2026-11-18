package br.com.ecommerce.order.order_service.consumer;

import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.event.StockReservedEvent;
import br.com.ecommerce.order.order_service.service.OrderStockService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockReservedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderStockService orderStockService;

    public StockReservedConsumer(OrderStockService orderStockService) {
        this.objectMapper = new ObjectMapper();
        this.orderStockService = orderStockService;
    }

    @KafkaListener(
            topics = "stock.reserved",
            groupId = "order-stock-group"
    )
    public void consume(String message) throws Exception {

        StockReservedEvent event = objectMapper.readValue(
                message,
                StockReservedEvent.class
        );

        log.info("Evento stock.reserved recebido: {}", event.orderId());

        orderStockService.confirmStockReservation(event.orderId());
    }
}
