package br.com.ecommerce.product.product_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import br.com.ecommerce.product.product_service.entity.Product;
import br.com.ecommerce.product.product_service.entity.StockReservation;
import br.com.ecommerce.product.product_service.event.OrderCreatedEvent;
import br.com.ecommerce.product.product_service.exception.StockRejectedException;
import br.com.ecommerce.product.product_service.repository.ProductRepository;
import br.com.ecommerce.product.product_service.repository.StockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockReservationService {

    private final ProductRepository productRepository;
    private final StockReservationRepository reservationRepository;
    private final StockOutboxService outboxService;
    private final StockAvailabilityPolicy availabilityPolicy;

    @Transactional
    public void reserve(OrderCreatedEvent event) {

        if (event.orderId() == null) {
            throw new IllegalArgumentException("Pedido sem ID");
        }

        if (reservationRepository.existsByOrderId(event.orderId())) {
            log.info("Pedido já reservado: {}", event.orderId());
            return;
        }

        if (event.items() == null || event.items().isEmpty()) {
            throw new IllegalArgumentException("Pedido sem itens");
        }

        Map<Long, Integer> quantities = new TreeMap<>();

        for (var item : event.items()) {

            if (item.productId() == null ||
                    item.quantity() == null ||
                    item.quantity() <= 0) {

                throw new IllegalArgumentException(
                        "Item inválido no pedido " + event.orderId()
                );
            }

            quantities.merge(
                    item.productId(),
                    item.quantity(),
                    Math::addExact
            );
        }

        // Valida os produtos e bloqueia os registros.
        Map<Long, Product> products = new TreeMap<>();

        for (var entry : quantities.entrySet()) {

            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productRepository
                    .findByIdForUpdate(productId)
                    .orElseThrow(() ->
                            new StockRejectedException(
                                    "Produto não encontrado: " + productId
                            )
                    );

            if (!availabilityPolicy.isAvailable(product, quantity)) {

                throw new StockRejectedException(
                        "Estoque insuficiente para produto: " + productId
                );
            }

            products.put(productId, product);
        }

        // Cria a reserva com os itens.
        StockReservation reservation =
                new StockReservation(event.orderId());

        for (var entry : quantities.entrySet()) {

            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = products.get(productId);

            // Reduz o estoque.
            product.setStock(
                    product.getStock() - quantity
            );

            // Registra qual produto foi reservado
            // e qual quantidade deverá ser devolvida.
            reservation.addItem(productId, quantity);
        }

        // Salva a reserva e seus itens na mesma transação.
        reservationRepository.save(reservation);

        outboxService.recordReserved(event.orderId());

        log.info("Estoque reservado para pedido: {}", event.orderId());
    }
}
