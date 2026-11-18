package br.com.ecommerce.product.product_service.service;
import br.com.ecommerce.product.product_service.entity.OutboxEvent;
import br.com.ecommerce.product.product_service.event.StockReservedEvent;
import br.com.ecommerce.product.product_service.event.StockRejectedEvent;
import br.com.ecommerce.product.product_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor
public class StockOutboxService {
 private final OutboxEventRepository repository;
 private final ObjectMapper mapper;
 public void recordReserved(Long orderId) {record("stock.reserved",orderId,new StockReservedEvent(orderId));}
 @Transactional
 public void recordRejected(Long orderId,String reason) {record("stock.rejected",orderId,new StockRejectedEvent(orderId,reason));}
 private void record(String topic,Long orderId,Object payload) {
  if(orderId==null) throw new IllegalArgumentException("orderId obrigatório");
  String key=orderId.toString();
  if(repository.existsByTopicAndEventKey(topic,key)) return;
  try {repository.save(new OutboxEvent(topic,key,mapper.writeValueAsString(payload)));}
  catch(JsonProcessingException ex) {throw new IllegalStateException("Falha serializando evento " + topic,ex);}
 }
}
