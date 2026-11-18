package br.com.ecommerce.payment.payment_service.service;

import br.com.ecommerce.payment.payment_service.repository.PaymentOutboxRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentOutboxDispatchService {
    private static final Logger log = LoggerFactory.getLogger(PaymentOutboxDispatchService.class);
    private final PaymentOutboxRepository repository;
    private final KafkaTemplate<String, Object> kafka;
    public PaymentOutboxDispatchService(PaymentOutboxRepository repository, KafkaTemplate<String, Object> kafka) {
        this.repository = repository;
        this.kafka = kafka;
    }
    @Scheduled(fixedDelayString = "${app.outbox.poll-ms:3000}")
    @Transactional
    public void dispatchBatch() {
        for (var event : repository.pendingForUpdate(PageRequest.of(0, 50))) {
            try {
                // Producer atual usa JsonSerializer: converter JSON para Map evita string JSON entre aspas.
                var payload = new com.fasterxml.jackson.databind.ObjectMapper().readValue(event.getPayload(), java.util.Map.class);
                kafka.send(event.getTopic(), event.getEventKey(), payload).get(10, TimeUnit.SECONDS);
                event.published();
            } catch (InterruptedException ex) {
                event.failed();
                Thread.currentThread().interrupt();
                log.error("Outbox interrompido id={}", event.getId(), ex);
                break;
            } catch (ExecutionException ex) {
                event.failed();
                if (ex.getCause() instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    log.error("Outbox interrompido id={}", event.getId(), ex);
                    break;
                }
                log.error("Falha Outbox id={}", event.getId(), ex);
            } catch (Exception ex) {
                event.failed();
                log.error("Falha Outbox id={}", event.getId(), ex);
            }
        }
    }
}
