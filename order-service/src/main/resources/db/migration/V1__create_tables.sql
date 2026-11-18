-- Flyway V1: order-service
CREATE TABLE orders (
 id BIGINT NOT NULL AUTO_INCREMENT,
 customer_id BIGINT NOT NULL,
 status ENUM('PENDING','STOCK_RESERVED','PAYMENT_APPROVED','CONFIRMED','SHIPPED','CANCELLED') NOT NULL,
 total_amount DECIMAL(15,2) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE order_items (
 id BIGINT NOT NULL AUTO_INCREMENT,
 order_id BIGINT NOT NULL,
 product_id BIGINT NOT NULL,
 quantity INT NOT NULL,
 unit_price DECIMAL(15,2) NOT NULL,
 PRIMARY KEY (id),
 INDEX idx_order_items_order_id (order_id),
 CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;

CREATE TABLE outbox_events (
 id BINARY(16) NOT NULL,
 topic VARCHAR(150) NOT NULL,
 event_key VARCHAR(150) NOT NULL,
 payload LONGTEXT NOT NULL,
 status ENUM('PENDING','PUBLISHED') NOT NULL,
 created_at DATETIME(6) NOT NULL,
 published_at DATETIME(6) NULL,
 attempts INT NOT NULL,
 PRIMARY KEY (id),
 INDEX idx_outbox_status_created (status, created_at)
) ENGINE=InnoDB;
