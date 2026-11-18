package br.com.ecommerce.product.product_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedDltConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedDltConsumer.class);

    @KafkaListener(topics = "order.created.DLT", groupId = "product-stock-dlt-group")
    public void consume(ConsumerRecord<String, String> record) {
        // Apenas registra a mensagem: não executa a reserva novamente automaticamente.
        log.error("Mensagem na DLT: topic={} partition={} offset={} key={} payload={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}
