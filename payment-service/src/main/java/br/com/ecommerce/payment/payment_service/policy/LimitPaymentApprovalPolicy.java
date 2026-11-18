package br.com.ecommerce.payment.payment_service.policy;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LimitPaymentApprovalPolicy
        implements PaymentApprovalPolicy {

    private final BigDecimal limit;

    public LimitPaymentApprovalPolicy(
            @Value("${payment.approval.limit:10000.00}")
            BigDecimal limit) {

        this.limit = limit;
    }

    @Override
    public boolean shouldApprove(BigDecimal amount) {
        return amount.compareTo(limit) <= 0;
    }
}