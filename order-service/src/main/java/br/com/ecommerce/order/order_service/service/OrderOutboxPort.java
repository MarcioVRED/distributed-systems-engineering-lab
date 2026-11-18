package br.com.ecommerce.order.order_service.service;
import br.com.ecommerce.order.order_service.entity.Order;
public interface OrderOutboxPort {
    void orderCreated(Order order);
    void paymentRequested(Order order);
    void orderCancelled(Long orderId);
}
