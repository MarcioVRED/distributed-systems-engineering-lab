package br.com.ecommerce.product.product_service.exception;

public class StockRejectedException extends RuntimeException {

    public StockRejectedException(String message) {
        super(message);
    }
}
