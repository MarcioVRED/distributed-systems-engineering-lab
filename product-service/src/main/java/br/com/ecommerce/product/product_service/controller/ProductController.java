package br.com.ecommerce.product.product_service.controller;

import lombok.RequiredArgsConstructor;

import br.com.ecommerce.product.product_service.entity.Product;
import br.com.ecommerce.product.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {

        Product createdProduct = productService.create(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {

        return ResponseEntity.ok(
                productService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {

        return ResponseEntity.ok(
                productService.findById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestBody Product product) {

        return ResponseEntity.ok(
                productService.update(id, product)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}