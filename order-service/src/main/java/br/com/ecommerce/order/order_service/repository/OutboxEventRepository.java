package br.com.ecommerce.order.order_service.repository;

import br.com.ecommerce.order.order_service.entity.OutboxEvent;
import br.com.ecommerce.order.order_service.entity.enums.OutboxStatus;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT e
        FROM OutboxEvent e
        WHERE e.status = :status
        ORDER BY e.createdAt ASC
        """)
    List<OutboxEvent> findEventsByStatus(
            @Param("status") OutboxStatus status,
            Pageable pageable
    );
}
