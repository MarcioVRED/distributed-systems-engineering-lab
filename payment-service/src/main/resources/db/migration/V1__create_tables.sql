-- Flyway V1: payment-service
CREATE TABLE payments (
 id BIGINT NOT NULL AUTO_INCREMENT,
 order_id BIGINT NOT NULL,
 amount DECIMAL(38,2) NOT NULL,
 status ENUM('PENDING','APPROVED','REJECTED') NOT NULL,
 created_at DATETIME(6) NOT NULL,
 PRIMARY KEY (id),
 CONSTRAINT uk_payments_order_id UNIQUE (order_id)
) ENGINE=InnoDB;

CREATE TABLE payment_outbox_events (
 id BINARY(16) NOT NULL,
 topic VARCHAR(255) NOT NULL,
 event_key VARCHAR(255) NOT NULL,
 payload LONGTEXT NOT NULL,
 status VARCHAR(255) NOT NULL,
 attempts INT NOT NULL,
 created_at DATETIME(6) NOT NULL,
 published_at DATETIME(6) NULL,
 PRIMARY KEY (id),
 CONSTRAINT uk_payment_outbox_topic_key UNIQUE (topic,event_key)
) ENGINE=InnoDB;
