
package br.com.ecommerce.order.order_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class OrderMetrics {

    private final Counter ordersCreated;
    private final Counter ordersCancelled;

    public OrderMetrics(MeterRegistry registry) {

        this.ordersCreated = Counter.builder("ecommerce.orders.created")
                .description("Total de pedidos criados")
                .register(registry);

        this.ordersCancelled = Counter.builder("ecommerce.orders.cancelled")
                .description("Total de pedidos cancelados")
                .register(registry);
    }

    public void orderCreated() {
        incrementAfterCommit(ordersCreated);
    }

    public void orderCancelled() {
        incrementAfterCommit(ordersCancelled);
    }

    private void incrementAfterCommit(Counter counter) {

        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {

            throw new IllegalStateException(
                    "Order metrics must be registered inside an active transaction"
            );
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        counter.increment();
                    }
                }
        );
    }
}
