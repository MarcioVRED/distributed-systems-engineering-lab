package br.com.ecommerce.product.product_service.controller;

import br.com.ecommerce.product.product_service.entity.Product;
import br.com.ecommerce.product.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {
    ProductService service = mock(ProductService.class);
    ProductController controller = new ProductController(service);

    @Test
    void endpoints() {
        Product p = new Product("A", BigDecimal.ONE, 3);
        when(service.create(p)).thenReturn(p);
        assertEquals(HttpStatus.CREATED, controller.create(p).getStatusCode());
        when(service.findAll()).thenReturn(List.of(p));
        assertEquals(1, controller.findAll().getBody().size());
        when(service.findById(1L)).thenReturn(p);
        assertSame(p, controller.findById(1L).getBody());
        when(service.update(1L, p)).thenReturn(p);
        assertSame(p, controller.update(1L, p).getBody());
        assertEquals(HttpStatus.NO_CONTENT, controller.delete(1L).getStatusCode());
        verify(service).delete(1L);
    }
}
