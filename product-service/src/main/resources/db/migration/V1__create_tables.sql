-- Flyway V1: product-service
CREATE TABLE products (
 id BIGINT NOT NULL AUTO_INCREMENT,
 name VARCHAR(255) NOT NULL,
 price DECIMAL(15,2) NOT NULL,
 stock INT NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE stock_reservations (
 id BIGINT NOT NULL AUTO_INCREMENT,
 order_id BIGINT NOT NULL,
 created_at DATETIME(6) NOT NULL,
 compensated BIT(1) NOT NULL,
 compensated_at DATETIME(6) NULL,
 PRIMARY KEY (id),
 CONSTRAINT uk_stock_reservations_order UNIQUE (order_id)
) ENGINE=InnoDB;

CREATE TABLE stock_reservation_items (
 id BIGINT NOT NULL AUTO_INCREMENT,
 reservation_id BIGINT NOT NULL,
 product_id BIGINT NOT NULL,
 quantity INT NOT NULL,
 PRIMARY KEY (id),
 INDEX idx_stock_reservation_items_reservation (reservation_id),
 CONSTRAINT fk_stock_reservation_items_reservation FOREIGN KEY (reservation_id) REFERENCES stock_reservations(id)
) ENGINE=InnoDB;

CREATE TABLE outbox_events (
 id BINARY(16) NOT NULL,
 topic VARCHAR(150) NOT NULL,
 event_key VARCHAR(150) NOT NULL,
 payload LONGTEXT NOT NULL,
 status VARCHAR(20) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 published_at DATETIME(6) NULL,
 attempts INT NOT NULL,
 PRIMARY KEY (id),
 CONSTRAINT uk_product_outbox_topic_key UNIQUE (topic,event_key),
 INDEX idx_product_outbox_status_created (status,created_at)
) ENGINE=InnoDB;
