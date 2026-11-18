
package br.com.ecommerce.order.order_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.jupiter.api.Assertions.*;

class OrderMetricsTest {

    @Test
    void shouldRegisterMetricsOnlyAfterCommit() {

        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        try {
            OrderMetrics metrics = new OrderMetrics(registry);

            Counter created = registry
                    .get("ecommerce.orders.created")
                    .counter();

            Counter cancelled = registry
                    .get("ecommerce.orders.cancelled")
                    .counter();

            TransactionSynchronizationManager.initSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(true);

            try {
                metrics.orderCreated();
                metrics.orderCancelled();

                // Antes do commit, os contadores devem permanecer zerados.
                assertEquals(0.0, created.count());
                assertEquals(0.0, cancelled.count());

                // Simula o callback executado após o commit.
                for (TransactionSynchronization synchronization :
                        TransactionSynchronizationManager.getSynchronizations()) {
                    synchronization.afterCommit();
                }

                // Após o commit, os contadores devem ser incrementados.
                assertEquals(1.0, created.count());
                assertEquals(1.0, cancelled.count());

            } finally {
                TransactionSynchronizationManager.clearSynchronization();
                TransactionSynchronizationManager.setActualTransactionActive(false);
            }

        } finally {
            registry.close();
        }
    }

    @Test
    void shouldRejectMetricsWithoutActiveTransaction() {

        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        try {
            OrderMetrics metrics = new OrderMetrics(registry);

            assertThrows(
                    IllegalStateException.class,
                    metrics::orderCreated
            );

            assertThrows(
                    IllegalStateException.class,
                    metrics::orderCancelled
            );

        } finally {
            registry.close();
        }
    }

    @Test
    void shouldRejectMetricsWithoutSynchronization() {

        SimpleMeterRegistry registry = new SimpleMeterRegistry();

        try {
            OrderMetrics metrics = new OrderMetrics(registry);

            TransactionSynchronizationManager.setActualTransactionActive(true);

            try {
                assertThrows(
                        IllegalStateException.class,
                        metrics::orderCreated
                );

            } finally {
                TransactionSynchronizationManager.setActualTransactionActive(false);
            }

        } finally {
            registry.close();
        }
    }
}
