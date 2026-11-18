package br.com.ecommerce.order.order_service.repository;

import br.com.ecommerce.order.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "items")
    List<Order> findByCustomerId(Long customerId);

    @EntityGraph(attributePaths = "items")
    Optional<Order> findWithItemsById(Long id);

    @Override
    @EntityGraph(attributePaths = "items")
    List<Order> findAll();
}