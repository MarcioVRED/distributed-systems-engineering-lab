package br.com.ecommerce.order.order_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.entity.enums.OrderStatus;
import br.com.ecommerce.order.order_service.repository.OrderRepository;
import br.com.ecommerce.order.order_service.metrics.OrderMetrics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderPaymentService {

    private final OrderRepository orderRepository;
    private final OrderOutboxPort outboxPort;
    private final OrderMetrics orderMetrics;

    @Transactional
    public void approvePayment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Pedido não encontrado: " + orderId
                        )
                );

        // Evita processamento duplicado.
        if (order.getStatus() == OrderStatus.PAYMENT_APPROVED ||
                order.getStatus() == OrderStatus.CONFIRMED) {

            log.info("Pagamento já aprovado para pedido: {}", orderId);
            return;
        }

        // Pagamento só pode ser aprovado após reserva de estoque.
        if (order.getStatus() != OrderStatus.STOCK_RESERVED) {

            log.info("{}", "Evento payment.approved ignorado. Pedido: "
                            + orderId
                            + " | Status atual: "
                            + order.getStatus());
            return;
        }

        order.setStatus(OrderStatus.PAYMENT_APPROVED);

        orderRepository.save(order);

        log.info("Pedido atualizado para PAYMENT_APPROVED: {}", orderId);
    }


    @Transactional
    public void rejectPayment(Long orderId, String reason) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Pedido não encontrado: " + orderId
                        )
                );

        // Evita processamento duplicado.
        if (order.getStatus() == OrderStatus.CANCELLED) {
            log.info("Pedido já cancelado: {}", orderId);
            return;
        }

        // Só cancela após reserva de estoque.
        if (order.getStatus() != OrderStatus.STOCK_RESERVED) {
            log.info(
                    "Evento payment.rejected ignorado. Pedido: {} | Status atual: {}",
                    orderId,
                    order.getStatus()
            );
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        // Registra o evento de cancelamento no Outbox.
        outboxPort.orderCancelled(orderId);

        // Incrementa a métrica somente após o commit.
        orderMetrics.orderCancelled();

        log.info(
                "Pedido cancelado por rejeição de pagamento: {} | Motivo: {}",
                orderId,
                reason
        );
    }
}
