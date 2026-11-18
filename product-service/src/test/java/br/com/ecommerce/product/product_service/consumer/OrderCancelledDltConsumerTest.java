package br.com.ecommerce.product.product_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OrderCancelledDltConsumerTest {

    @Test
    void logsDeadLetterWithoutReprocessingCompensation() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "order.cancelled.DLT", 2, 15L, "9", "invalid-payload");

        assertDoesNotThrow(() -> new OrderCancelledDltConsumer().consume(record));
    }
}
