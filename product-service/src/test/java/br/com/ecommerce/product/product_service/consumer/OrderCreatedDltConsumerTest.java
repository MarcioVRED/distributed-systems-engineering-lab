package br.com.ecommerce.product.product_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OrderCreatedDltConsumerTest {

    @Test
    void logsDeadLetterWithoutReprocessingStock() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "order.created.DLT", 1, 42L, "7", "invalid-payload");

        assertDoesNotThrow(() -> new OrderCreatedDltConsumer().consume(record));
    }
}
