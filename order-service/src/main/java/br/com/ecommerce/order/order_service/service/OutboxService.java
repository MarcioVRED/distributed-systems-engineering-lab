package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.OutboxEvent;
import br.com.ecommerce.order.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(
            String topic,
            String eventKey,
            String payload
    ) {

        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException(
                    "O tópico Kafka é obrigatório"
            );
        }

        if (eventKey == null || eventKey.isBlank()) {
            throw new IllegalArgumentException(
                    "A chave do evento é obrigatória"
            );
        }

        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException(
                    "O payload do evento é obrigatório"
            );
        }

        OutboxEvent event = new OutboxEvent(
                topic,
                eventKey,
                payload
        );

        outboxEventRepository.save(event);

        log.info(
                "Evento registrado no Outbox. eventId={}, topic={}, eventKey={}",
                event.getId(),
                topic,
                eventKey
        );
    }
}