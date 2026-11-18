package br.com.ecommerce.product.product_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class KafkaRetryDltConfigTest {

    @Test
    void createsErrorHandlerForRetryAndDlt() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

        DefaultErrorHandler handler = new KafkaRetryDltConfig().kafkaErrorHandler(kafkaTemplate);

        assertNotNull(handler);
    }
}
