package br.com.ecommerce.order.order_service.producer;

import lombok.RequiredArgsConstructor;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.event.OrderPersistedEvent;
import br.com.ecommerce.order.order_service.repository.OrderRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
public class OrderCreatedListener {

    private final OrderRepository orderRepository;
    private final OrderCreatedPort eventPublisher;


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onOrderCreated(OrderPersistedEvent event) {

        Order order = orderRepository.findWithItemsById(event.orderId())
                .orElseThrow();

        eventPublisher.publish(order);
    }
}