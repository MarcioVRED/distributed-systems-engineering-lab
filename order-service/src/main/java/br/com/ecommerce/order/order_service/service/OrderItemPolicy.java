package br.com.ecommerce.order.order_service.service;
import br.com.ecommerce.order.order_service.entity.OrderItem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
@Component
public class OrderItemPolicy {
    public void validate(OrderItem item) {
        if (item == null || item.getProductId() == null || item.getQuantity() == null ||
            item.getQuantity() <= 0 || item.getUnitPrice() == null ||
            item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order item");
        }
    }
    public BigDecimal subtotal(OrderItem item) {
        validate(item);
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
