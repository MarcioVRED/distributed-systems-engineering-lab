package br.com.ecommerce.product.product_service.service;

import lombok.RequiredArgsConstructor;

import br.com.ecommerce.product.product_service.entity.Product;
import br.com.ecommerce.product.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + id));
    }

    public Product update(Long id, Product product) {

        Product existingProduct = findById(id);

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());

        return productRepository.save(existingProduct);
    }

    public void delete(Long id) {

        Product product = findById(id);

        productRepository.delete(product);
    }
}