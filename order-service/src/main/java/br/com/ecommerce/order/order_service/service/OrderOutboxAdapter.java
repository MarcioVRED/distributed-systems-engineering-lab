package br.com.ecommerce.order.order_service.service;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.event.OrderCreatedEvent;
import br.com.ecommerce.order.order_service.event.PaymentRequestedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class OrderOutboxAdapter implements OrderOutboxPort {
    private final OutboxService outboxService;
    private final ObjectMapper mapper = new ObjectMapper();
    private void append(String topic, Long id, Object event) {
        try {
            outboxService.saveEvent(topic, id.toString(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Falha ao serializar evento " + topic, ex);
        }
    }
    @Override public void orderCreated(Order order) {
        var items = order.getItems().stream().map(i -> new OrderCreatedEvent.OrderItemEvent(i.getProductId(), i.getQuantity())).toList();
        append("order.created", order.getId(), new OrderCreatedEvent(order.getId(), order.getCustomerId(), order.getTotalAmount(), items));
    }
    @Override public void paymentRequested(Order order) {
        append("payment.requested", order.getId(), new PaymentRequestedEvent(order.getId(), order.getTotalAmount()));
    }
    @Override public void orderCancelled(Long orderId) {
        append("order.cancelled", orderId, Map.of("orderId", orderId));
    }
}
