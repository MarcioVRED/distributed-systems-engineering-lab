package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class DefaultStockAvailabilityPolicy implements StockAvailabilityPolicy {
    @Override
    public boolean isAvailable(Product product, int requestedQuantity) {
        return product != null && product.getStock() != null
                && requestedQuantity > 0 && product.getStock() >= requestedQuantity;
    }
}
