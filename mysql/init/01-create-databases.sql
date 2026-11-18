-- Bancos de dados
CREATE DATABASE IF NOT EXISTS ecommerce_product;
CREATE DATABASE IF NOT EXISTS ecommerce_customer;
CREATE DATABASE IF NOT EXISTS ecommerce_order;
CREATE DATABASE IF NOT EXISTS ecommerce_payment;

-- Usuários de cada microsserviço
CREATE USER IF NOT EXISTS 'product_user'@'%'
IDENTIFIED BY 'product_local_dev_only';

CREATE USER IF NOT EXISTS 'customer_user'@'%'
IDENTIFIED BY 'customer_local_dev_only';

CREATE USER IF NOT EXISTS 'order_user'@'%'
IDENTIFIED BY 'order_local_dev_only';

CREATE USER IF NOT EXISTS 'payment_user'@'%'
IDENTIFIED BY 'payment_local_dev_only';

-- Permissões
GRANT ALL PRIVILEGES ON ecommerce_product.*
TO 'product_user'@'%';

GRANT ALL PRIVILEGES ON ecommerce_customer.*
TO 'customer_user'@'%';

GRANT ALL PRIVILEGES ON ecommerce_order.*
TO 'order_user'@'%';

GRANT ALL PRIVILEGES ON ecommerce_payment.*
TO 'payment_user'@'%';
