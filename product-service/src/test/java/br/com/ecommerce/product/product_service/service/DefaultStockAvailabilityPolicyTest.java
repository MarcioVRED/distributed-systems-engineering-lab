package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class DefaultStockAvailabilityPolicyTest {
    private final StockAvailabilityPolicy policy = new DefaultStockAvailabilityPolicy();

    @Test
    void availabilityBoundaries() {
        Product p = new Product("A", BigDecimal.ONE, 3);
        assertTrue(policy.isAvailable(p, 3));
        assertTrue(policy.isAvailable(p, 1));
        assertFalse(policy.isAvailable(p, 4));
        assertFalse(policy.isAvailable(p, 0));
        assertFalse(policy.isAvailable(p, -1));
        assertFalse(policy.isAvailable(null, 1));
        p.setStock(null);
        assertFalse(policy.isAvailable(p, 1));
    }
}
