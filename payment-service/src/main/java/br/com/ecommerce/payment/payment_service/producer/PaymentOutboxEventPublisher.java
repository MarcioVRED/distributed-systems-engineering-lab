package br.com.ecommerce.payment.payment_service.producer;

import br.com.ecommerce.payment.payment_service.entity.PaymentOutboxEvent;
import br.com.ecommerce.payment.payment_service.repository.PaymentOutboxRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
@Primary
public class PaymentOutboxEventPublisher implements PaymentEventPort {
    private final PaymentOutboxRepository repository;
    public PaymentOutboxEventPublisher(PaymentOutboxRepository repository) { this.repository = repository; }

    @Override public void publishApproved(Long orderId) {
        save("payment.approved", orderId, "{\"orderId\":" + orderId + "}");
    }
    @Override public void publishRejected(Long orderId) {
        save("payment.rejected", orderId,
                "{\"orderId\":" + orderId + ",\"reason\":\"Pagamento rejeitado pela regra simulada\"}");
    }
    private void save(String topic, Long orderId, String payload) {
        Objects.requireNonNull(orderId, "orderId");
        String key = orderId.toString();
        if (!repository.existsByTopicAndEventKey(topic, key)) {
            repository.save(new PaymentOutboxEvent(topic, key, payload));
        }
    }
}
