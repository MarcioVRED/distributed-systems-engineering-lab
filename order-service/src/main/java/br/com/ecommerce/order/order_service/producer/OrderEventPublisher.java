package br.com.ecommerce.order.order_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.event.OrderCreatedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
public class OrderEventPublisher implements OrderCreatedPort {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;


    @Override
    public void publish(Order order) {

        var items = order.getItems()
                .stream()
                .map(item -> new OrderCreatedEvent.OrderItemEvent(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();

        var event = new OrderCreatedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                items
        );

        log.info("Enviando pedido ao Kafka: {}", event);

        kafkaTemplate.send(
                "order.created",
                order.getId().toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error("ERRO ao publicar no Kafka: {}", exception.getMessage());
            } else {
                log.info("{}", "Mensagem publicada no Kafka! Partition: "
                                + result.getRecordMetadata().partition()
                                + " Offset: "
                                + result.getRecordMetadata().offset());
            }
        });
    }
}