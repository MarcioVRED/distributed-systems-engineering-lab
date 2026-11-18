package br.com.ecommerce.product.product_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "stock_reservation_items")
public class StockReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private StockReservation reservation;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    public StockReservationItem(
            StockReservation reservation,
            Long productId,
            Integer quantity) {

        this.reservation = reservation;
        this.productId = productId;
        this.quantity = quantity;
    }
}
