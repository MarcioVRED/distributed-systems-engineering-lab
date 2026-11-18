package br.com.ecommerce.order.order_service.service;
import br.com.ecommerce.order.order_service.entity.OutboxEvent;
import br.com.ecommerce.order.order_service.entity.enums.OutboxStatus;
import br.com.ecommerce.order.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxDispatchService {
    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    // Pessimistic lock protects a batch while publishing. Kafka ack may precede DB commit:
    // duplicates remain possible after crashes; consumers must be idempotent.
    @Transactional
    public void dispatchBatch() {
        var pending = repository.findEventsByStatus(OutboxStatus.PENDING, PageRequest.of(0, 50));
        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload())
                        .get(10, TimeUnit.SECONDS);
                event.markPublished();
                log.info("Outbox publicado id={} topic={} key={}", event.getId(), event.getTopic(), event.getEventKey());
            } catch (InterruptedException ex) {
                event.registerFailure();
                Thread.currentThread().interrupt();
                log.error("Outbox interrompido id={}", event.getId(), ex);
                break;
            } catch (ExecutionException ex) {
                event.registerFailure();
                if (ex.getCause() instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    log.error("Outbox interrompido id={}", event.getId(), ex);
                    break;
                }
                log.error("Falha Outbox id={} attempts={}", event.getId(), event.getAttempts(), ex);
            } catch (Exception ex) {
                event.registerFailure();
                log.error("Falha Outbox id={} attempts={}", event.getId(), event.getAttempts(), ex);
            }
        }
    }
}
