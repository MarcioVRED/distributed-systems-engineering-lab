package br.com.ecommerce.order.order_service.service;

import lombok.RequiredArgsConstructor;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.entity.OrderItem;
import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import br.com.ecommerce.order.order_service.repository.OrderRepository;
import br.com.ecommerce.order.order_service.metrics.OrderMetrics;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemPolicy itemPolicy;
    private final OrderOutboxPort outboxPort;
    private final OrderMetrics orderMetrics;

    @Transactional
    public Order create(Order order) {

        if (order.getCustomerId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Customer ID is required"
            );
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order must contain at least one item"
            );
        }

        BigDecimal total = BigDecimal.ZERO;

        List<OrderItem> items = List.copyOf(order.getItems());

        // Recria a associação entre pedido e itens
        order.getItems().clear();

        for (OrderItem item : items) {
            BigDecimal subtotal = itemPolicy.subtotal(item);
            total = total.add(subtotal);
            order.addItem(item);
        }

        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);
        outboxPort.orderCreated(savedOrder);
        orderMetrics.orderCreated();
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {

        return orderRepository.findWithItemsById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Order cancel(Long id) {

        Order order = findById(id);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only pending orders can be cancelled"
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        orderMetrics.orderCancelled();
        return savedOrder;
    }
}