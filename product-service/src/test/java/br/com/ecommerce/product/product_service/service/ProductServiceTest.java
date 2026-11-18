package br.com.ecommerce.product.product_service.service;

import br.com.ecommerce.product.product_service.entity.Product;
import br.com.ecommerce.product.product_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    ProductRepository repo = mock(ProductRepository.class);
    ProductService service = new ProductService(repo);

    Product p() {
        return new Product("A", BigDecimal.TEN, 5);
    }

    @Test
    void createAndList() {
        Product p = p();
        when(repo.save(p)).thenReturn(p);
        assertSame(p, service.create(p));
        when(repo.findAll()).thenReturn(List.of(p));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void findExistingAndMissing() {
        Product p = p();
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        assertSame(p, service.findById(1L));
        assertThrows(RuntimeException.class, () -> service.findById(2L));
    }

    @Test
    void update() {
        Product old = p();
        Product changed = new Product("B", BigDecimal.ONE, 7);
        when(repo.findById(1L)).thenReturn(Optional.of(old));
        when(repo.save(old)).thenReturn(old);
        assertSame(old, service.update(1L, changed));
        assertEquals("B", old.getName());
        assertEquals(BigDecimal.ONE, old.getPrice());
        assertEquals(7, old.getStock());
    }

    @Test
    void delete() {
        Product p = p();
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        service.delete(1L);
        verify(repo).delete(p);
        assertThrows(RuntimeException.class, () -> service.delete(2L));
    }
}
