package br.com.ecommerce.payment.payment_service.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaRetryDltConfigTest {
    @Test
    void createsErrorHandler() {
        assertNotNull(new KafkaRetryDltConfig().kafkaErrorHandler("localhost:9092"));
    }
}
