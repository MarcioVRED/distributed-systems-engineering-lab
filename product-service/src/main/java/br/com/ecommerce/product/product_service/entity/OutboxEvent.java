package br.com.ecommerce.product.product_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "outbox_events", uniqueConstraints = @UniqueConstraint(name = "uk_product_outbox_topic_key", columnNames = {"topic", "event_key"}), indexes = @Index(name = "idx_product_outbox_status_created", columnList = "status,created_at"))
public class OutboxEvent {
    @Id
    private UUID id;
    @Column(nullable = false, length = 150)
    private String topic;
    @Column(name = "event_key", nullable = false, length = 150)
    private String eventKey;
    @Column(name = "payload", columnDefinition = "LONGTEXT", nullable = false)
    private String payload;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    @Column(nullable = false)
    private int attempts;

    public OutboxEvent(String topic, String eventKey, String payload) {
        this.id = UUID.randomUUID();
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public void markPublished() {
        this.status = "PUBLISHED";
        this.publishedAt = LocalDateTime.now();
    }

    public void registerFailure() {
        this.attempts++;
    }
}
