package br.com.ecommerce.payment.payment_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_outbox_events", uniqueConstraints =
        @UniqueConstraint(name = "uk_payment_outbox_topic_key", columnNames = {"topic", "event_key"}))
public class PaymentOutboxEvent {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String topic;
    @Column(name = "event_key", nullable = false)
    private String eventKey;
    @Column(name = "payload", columnDefinition = "LONGTEXT", nullable = false)
    private String payload;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private int attempts;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    protected PaymentOutboxEvent() {}
    public PaymentOutboxEvent(String topic, String eventKey, String payload) {
        this.id = UUID.randomUUID();
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
    public UUID getId() { return id; }
    public String getTopic() { return topic; }
    public String getEventKey() { return eventKey; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public void published() { status = "PUBLISHED"; publishedAt = LocalDateTime.now(); }
    public void failed() { attempts++; }
}
