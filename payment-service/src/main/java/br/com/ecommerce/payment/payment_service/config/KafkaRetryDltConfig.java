package br.com.ecommerce.payment.payment_service.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;
import java.util.Map;

@Configuration
public class KafkaRetryDltConfig {
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            @Value("${spring.kafka.bootstrap-servers}") String brokers) {
        var factory = new DefaultKafkaProducerFactory<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        ));
        var dltTemplate = new KafkaTemplate<>(factory);
        var recoverer = new DeadLetterPublishingRecoverer(dltTemplate,
                (record, exception) -> new org.apache.kafka.common.TopicPartition(
                        record.topic() + ".DLT", record.partition()));
        // 1 tentativa inicial + 2 novas tentativas, intervalo 2s.
        return new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2L));
    }
}
