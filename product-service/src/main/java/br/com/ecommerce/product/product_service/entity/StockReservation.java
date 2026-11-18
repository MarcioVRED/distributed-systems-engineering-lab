package br.com.ecommerce.product.product_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "stock_reservations",
        uniqueConstraints = @UniqueConstraint(
                columnNames = "order_id"
        )
)
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean compensated = false;

    private LocalDateTime compensatedAt;

    @OneToMany(
            mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StockReservationItem> items = new ArrayList<>();


    public StockReservation(Long orderId) {
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
        this.compensated = false;
    }

    public void addItem(Long productId, Integer quantity) {

        if (productId == null ||
                quantity == null ||
                quantity <= 0) {

            throw new IllegalArgumentException(
                    "Produto ou quantidade inválida"
            );
        }

        StockReservationItem item =
                new StockReservationItem(
                        this,
                        productId,
                        quantity
                );

        this.items.add(item);
    }

    public void compensate() {

        if (this.compensated) {
            return;
        }

        this.compensated = true;
        this.compensatedAt = LocalDateTime.now();
    }
}
