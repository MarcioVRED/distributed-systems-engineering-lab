package br.com.ecommerce.order.order_service.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaRetryDltConfigTest {
    @Test
    void createsErrorHandler() {
        assertNotNull(new KafkaRetryDltConfig().kafkaErrorHandler("localhost:9092"));
    }
}
