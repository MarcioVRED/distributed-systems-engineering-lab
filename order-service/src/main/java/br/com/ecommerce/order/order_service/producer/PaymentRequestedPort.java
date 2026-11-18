package br.com.ecommerce.order.order_service.producer;
import java.math.BigDecimal;
public interface PaymentRequestedPort { void publishPaymentRequested(Long orderId, BigDecimal amount); }
