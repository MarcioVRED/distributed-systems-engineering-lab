-- Flyway V1: customer-service
CREATE TABLE customers (
 id BIGINT NOT NULL AUTO_INCREMENT,
 name VARCHAR(255) NOT NULL,
 email VARCHAR(255) NOT NULL,
 PRIMARY KEY (id),
 CONSTRAINT uk_customers_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE addresses (
 id BIGINT NOT NULL AUTO_INCREMENT,
 street VARCHAR(255) NOT NULL,
 city VARCHAR(255) NOT NULL,
 state VARCHAR(255) NOT NULL,
 zip_code VARCHAR(255) NOT NULL,
 customer_id BIGINT NOT NULL,
 PRIMARY KEY (id),
 INDEX idx_addresses_customer_id (customer_id),
 CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;
