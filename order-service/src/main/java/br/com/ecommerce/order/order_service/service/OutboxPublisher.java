package br.com.ecommerce.order.order_service.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix="outbox.publisher", name="enabled", havingValue="true")
public class OutboxPublisher {
    private final OutboxDispatchService dispatcher;
    @Scheduled(fixedDelayString="${outbox.publisher.fixed-delay-ms:3000}")
    public void publishPendingEvents() { dispatcher.dispatchBatch(); }
}
