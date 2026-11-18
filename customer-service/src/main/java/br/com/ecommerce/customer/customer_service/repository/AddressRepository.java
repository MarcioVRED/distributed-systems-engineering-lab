package br.com.ecommerce.customer.customer_service.repository;

import br.com.ecommerce.customer.customer_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    List<Address> findByCustomerId(Long customerId);
}
