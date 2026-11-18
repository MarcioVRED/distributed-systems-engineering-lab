package br.com.ecommerce.product.product_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import br.com.ecommerce.product.product_service.entity.Product;
import br.com.ecommerce.product.product_service.entity.StockReservation;
import br.com.ecommerce.product.product_service.entity.StockReservationItem;
import br.com.ecommerce.product.product_service.repository.ProductRepository;
import br.com.ecommerce.product.product_service.repository.StockReservationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockCompensationService {

    private final ProductRepository productRepository;
    private final StockReservationRepository reservationRepository;

    @Transactional
    public void compensate(Long orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException(
                    "ID do pedido não informado"
            );
        }

        // Bloqueia a reserva até o fim da transação.
        StockReservation reservation = reservationRepository
                .findByOrderIdForUpdate(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Reserva não encontrada para pedido: " + orderId
                        )
                );

        // Impede a devolução duplicada.
        if (reservation.isCompensated()) {
            log.info("Estoque já compensado para pedido: {}", orderId);
            return;
        }

        // Carrega os itens da reserva.
        List<StockReservationItem> items = reservation.getItems();

        // Protege reservas antigas sem itens registrados.
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException(
                    "Reserva sem itens registrados. Pedido: " + orderId
            );
        }

        // Ordena os produtos para manter uma ordem
        // consistente de aquisição dos bloqueios.
        List<StockReservationItem> sortedItems = items.stream()
                .sorted(
                        Comparator.comparing(
                                StockReservationItem::getProductId
                        )
                )
                .toList();

        // Devolve os produtos ao estoque.
        for (StockReservationItem item : sortedItems) {

            Product product = productRepository
                    .findByIdForUpdate(item.getProductId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Produto não encontrado: "
                                            + item.getProductId()
                            )
                    );

            if (item.getQuantity() == null ||
                    item.getQuantity() <= 0) {

                throw new IllegalStateException(
                        "Quantidade inválida na reserva: " + orderId
                );
            }

            product.setStock(
                    Math.addExact(
                            product.getStock(),
                            item.getQuantity()
                    )
            );

            log.info("Estoque devolvido: pedido={}, produto={}, quantidade={}", orderId, item.getProductId(), item.getQuantity());
        }

        // Marca a compensação na mesma transação.
        reservation.compensate();

        reservationRepository.save(reservation);

        log.info("Compensação concluída para pedido: {}", orderId);
    }
}
