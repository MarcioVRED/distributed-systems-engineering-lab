package br.com.ecommerce.product.product_service.repository;

import br.com.ecommerce.product.product_service.entity.StockReservation;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockReservationRepository
        extends JpaRepository<StockReservation, Long> {

    boolean existsByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM StockReservation r
            WHERE r.orderId = :orderId
            """)
    Optional<StockReservation> findByOrderIdForUpdate(
            @Param("orderId") Long orderId
    );
}
