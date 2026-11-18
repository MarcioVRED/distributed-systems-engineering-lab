package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.Product;

/** Domain policy for stock availability. */
public interface StockAvailabilityPolicy {
    boolean isAvailable(Product product, int requestedQuantity);
}
