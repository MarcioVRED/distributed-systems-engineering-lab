package br.com.ecommerce.order.order_service.producer;
import br.com.ecommerce.order.order_service.entity.Order;
public interface OrderCreatedPort { void publish(Order order); }
