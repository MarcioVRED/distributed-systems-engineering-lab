
package br.com.ecommerce.payment.payment_service.policy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LimitPaymentApprovalPolicyTest {

    private final PaymentApprovalPolicy policy =
            new LimitPaymentApprovalPolicy(
                    new BigDecimal("10000.00")
            );

    @Test
    void approvesBelowAndAtLimit() {
        assertTrue(policy.shouldApprove(
                new BigDecimal("1")
        ));

        assertTrue(policy.shouldApprove(
                new BigDecimal("10000.00")
        ));
    }

    @Test
    void rejectsAboveLimit() {
        assertFalse(policy.shouldApprove(
                new BigDecimal("10000.01")
        ));
    }

    @Test
    void approvesWithCustomLimit() {
        PaymentApprovalPolicy customPolicy =
                new LimitPaymentApprovalPolicy(
                        new BigDecimal("50000.00")
                );

        assertTrue(customPolicy.shouldApprove(
                new BigDecimal("44000.00")
        ));

        assertFalse(customPolicy.shouldApprove(
                new BigDecimal("50000.01")
        ));
    }
}
