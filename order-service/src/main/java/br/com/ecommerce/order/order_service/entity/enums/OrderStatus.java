package br.com.ecommerce.order.order_service.entity.enums;

public enum OrderStatus {
    PENDING,
    STOCK_RESERVED,
    PAYMENT_APPROVED,
    CONFIRMED,
    SHIPPED,
    CANCELLED
}