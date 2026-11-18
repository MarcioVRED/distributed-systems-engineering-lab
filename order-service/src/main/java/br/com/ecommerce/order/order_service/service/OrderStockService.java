
package br.com.ecommerce.order.order_service.service;

import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import br.com.ecommerce.order.order_service.metrics.OrderMetrics;
import br.com.ecommerce.order.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderStockService {

    private final OrderRepository orderRepository;
    private final OrderOutboxPort outboxPort;
    private final OrderMetrics orderMetrics;

    @Transactional
    public void confirmStockReservation(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Pedido não encontrado: " + orderId
                        )
                );

        if (order.getStatus() == OrderStatus.STOCK_RESERVED) {
            log.info("Pedido já possui estoque reservado: {}", orderId);
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            log.info(
                    "Evento stock.reserved ignorado. Status atual: {}",
                    order.getStatus()
            );
            return;
        }

        order.setStatus(OrderStatus.STOCK_RESERVED);

        orderRepository.save(order);

        outboxPort.paymentRequested(order);

        log.info("Pedido atualizado para STOCK_RESERVED: {}", orderId);
    }

    @Transactional
    public void rejectStockReservation(Long orderId, String reason) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Pedido não encontrado: " + orderId
                        )
                );

        // Idempotência: não processar novamente pedidos cancelados.
        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.info("Pedido já cancelado: {}", orderId);
            return;
        }

        // Só permite cancelamento de pedidos pendentes.
        if (order.getStatus() != OrderStatus.PENDING) {
            log.info(
                    "Evento stock.rejected ignorado. Status atual: {}",
                    order.getStatus()
            );
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        // Registra a métrica somente após o commit da transação.
        orderMetrics.orderCancelled();

        log.info(
                "Pedido cancelado por rejeição de estoque: {} | Motivo: {}",
                orderId,
                reason
        );
    }
}
