package br.com.ecommerce.customer.customer_service.service;

import br.com.ecommerce.customer.customer_service.entity.Address;
import br.com.ecommerce.customer.customer_service.entity.Customer;
import br.com.ecommerce.customer.customer_service.policy.CustomerEmailPolicy;
import br.com.ecommerce.customer.customer_service.repository.AddressRepository;
import br.com.ecommerce.customer.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CustomerEmailPolicy emailPolicy;

    @Transactional
    public Customer create(Customer customer) {
        emailPolicy.validateForCreate(customer.getEmail());
        Customer saved = customerRepository.save(customer);
        log.info("Cliente criado: customerId={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found: " + id));
    }

    @Transactional
    public Address addAddress(Long customerId, Address address) {
        Customer customer = findById(customerId);
        customer.addAddress(address);
        customerRepository.save(customer);
        log.info("Endereço adicionado: customerId={}", customerId);
        return address;
    }

    @Transactional(readOnly = true)
    public List<Address> findAddresses(Long customerId) {
        findById(customerId);
        return addressRepository.findByCustomerId(customerId);
    }

    @Transactional
    public Customer update(Long id, Customer customer) {
        Customer existing = findById(id);
        emailPolicy.validateForUpdate(existing.getEmail(), customer.getEmail());
        existing.setName(customer.getName());
        existing.setEmail(customer.getEmail());
        Customer saved = customerRepository.save(existing);
        log.info("Cliente atualizado: customerId={}", id);
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
        log.info("Cliente excluído: customerId={}", id);
    }
}
