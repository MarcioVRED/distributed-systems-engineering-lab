package br.com.ecommerce.customer.customer_service.repository;

import br.com.ecommerce.customer.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
}
