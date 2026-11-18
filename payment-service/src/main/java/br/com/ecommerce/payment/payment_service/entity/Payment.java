package br.com.ecommerce.payment.payment_service.entity;

import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@lombok.Getter
@lombok.NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(
        name = "payments",
        uniqueConstraints = @UniqueConstraint(
                columnNames = "order_id"
        )
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;


    public Payment(Long orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }






    public void approve() {
        this.status = PaymentStatus.APPROVED;
    }

    public void reject() {
        this.status = PaymentStatus.REJECTED;
    }
}
