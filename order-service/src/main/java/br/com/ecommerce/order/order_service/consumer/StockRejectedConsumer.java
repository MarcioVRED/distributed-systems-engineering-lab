package br.com.ecommerce.order.order_service.consumer;

import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.event.StockRejectedEvent;
import br.com.ecommerce.order.order_service.service.OrderStockService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StockRejectedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderStockService orderStockService;

    public StockRejectedConsumer(OrderStockService orderStockService) {
        this.objectMapper = new ObjectMapper();
        this.orderStockService = orderStockService;
    }

    @KafkaListener(
            topics = "stock.rejected",
            groupId = "order-stock-group"
    )
    public void consume(String message) throws Exception {

        StockRejectedEvent event = objectMapper.readValue(
                message,
                StockRejectedEvent.class
        );

        log.info("{}", "Evento stock.rejected recebido: "
                        + event.orderId()
                        + " | "
                        + event.reason());

        orderStockService.rejectStockReservation(
                event.orderId(),
                event.reason()
        );
    }
}
