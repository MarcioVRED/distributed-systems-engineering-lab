package br.com.ecommerce.payment.payment_service.policy;

import java.math.BigDecimal;

public interface PaymentApprovalPolicy {
    boolean shouldApprove(BigDecimal amount);
}
