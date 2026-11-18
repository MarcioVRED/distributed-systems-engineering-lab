package br.com.ecommerce.product.product_service.repository;
import br.com.ecommerce.product.product_service.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;
public interface OutboxEventRepository extends JpaRepository<OutboxEvent,UUID> {
 boolean existsByTopicAndEventKey(String topic,String eventKey);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select e from OutboxEvent e where e.status = 'PENDING' order by e.createdAt")
 List<OutboxEvent> findPendingForUpdate(Pageable pageable);
}
