package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockOutboxDispatchService {

 private final OutboxEventRepository repository;
 private final KafkaTemplate<String, String> kafkaTemplate;

 // O ack do Kafka pode anteceder o commit H2.
 // Os consumidores devem continuar idempotentes.
 @Scheduled(fixedDelayString = "${app.outbox.poll-ms:3000}")
 @Transactional
 public void dispatchBatch() {

  for (var event : repository.findPendingForUpdate(PageRequest.of(0, 50))) {

   try {
    kafkaTemplate.send(
            event.getTopic(),
            event.getEventKey(),
            event.getPayload()
    ).get(10, TimeUnit.SECONDS);

    event.markPublished();

    log.info(
            "Outbox publicado id={} topic={} order={}",
            event.getId(),
            event.getTopic(),
            event.getEventKey()
    );

   } catch (InterruptedException ex) {

    event.registerFailure();
    Thread.currentThread().interrupt();

    log.error("Outbox interrompido id={}", event.getId(), ex);
    break;

   } catch (ExecutionException ex) {

    event.registerFailure();

    if (ex.getCause() instanceof InterruptedException) {
     Thread.currentThread().interrupt();

     log.error(
             "Outbox interrompido durante publicação id={}",
             event.getId(),
             ex
     );
     break;
    }

    log.error(
            "Falha publicando Outbox id={}",
            event.getId(),
            ex
    );

   } catch (Exception ex) {

    event.registerFailure();

    log.error(
            "Falha publicando Outbox id={}",
            event.getId(),
            ex
    );
   }
  }
 }
}
