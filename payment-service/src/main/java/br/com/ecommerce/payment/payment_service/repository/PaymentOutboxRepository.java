package br.com.ecommerce.payment.payment_service.repository;

import br.com.ecommerce.payment.payment_service.entity.PaymentOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutboxEvent, UUID> {
    boolean existsByTopicAndEventKey(String topic, String eventKey);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from PaymentOutboxEvent e where e.status = 'PENDING' order by e.createdAt")
    List<PaymentOutboxEvent> pendingForUpdate(Pageable pageable);
}
