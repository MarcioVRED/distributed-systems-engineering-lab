package br.com.ecommerce.payment.payment_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DeadLetterLogConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterLogConsumer.class);

    @KafkaListener(topics = {"payment.requested.DLT"},
                   groupId = "payment-dlt-monitor-group")
    public void receive(ConsumerRecord<String, String> record) {
        log.error("Mensagem DLT aguardando análise: topic={} partition={} offset={} key={} payload={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}
