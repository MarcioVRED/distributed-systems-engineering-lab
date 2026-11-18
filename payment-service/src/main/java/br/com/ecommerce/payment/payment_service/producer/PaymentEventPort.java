package br.com.ecommerce.payment.payment_service.producer;
public interface PaymentEventPort {
    void publishApproved(Long orderId);
    void publishRejected(Long orderId);
}
