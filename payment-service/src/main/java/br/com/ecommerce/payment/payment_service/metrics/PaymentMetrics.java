
package br.com.ecommerce.payment.payment_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class PaymentMetrics {

    private final Counter approvedCounter;
    private final Counter rejectedCounter;

    public PaymentMetrics(MeterRegistry registry) {

        this.approvedCounter = Counter.builder(
                        "ecommerce.payments.approved"
                ).description("Total de pagamentos aprovados")
                .register(registry);

        this.rejectedCounter = Counter.builder(
                        "ecommerce.payments.rejected"
                ).description("Total de pagamentos rejeitados")
                .register(registry);
    }

    public void approved() {
        approvedCounter.increment();
    }

    public void rejected() {
        rejectedCounter.increment();
    }
}
