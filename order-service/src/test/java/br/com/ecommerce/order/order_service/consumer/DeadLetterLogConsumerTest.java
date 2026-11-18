package br.com.ecommerce.order.order_service.consumer;

import org.junit.jupiter.api.Test;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static org.junit.jupiter.api.Assertions.*;

class DeadLetterLogConsumerTest {
    @Test
    void logsDltWithoutThrowing() {
        var consumer = new DeadLetterLogConsumer();
        assertDoesNotThrow(() -> consumer.receive(new ConsumerRecord<>("test.DLT", 0, 1L, "1", "invalid")));
    }
}
