package br.com.ecommerce.customer.customer_service.controller;

import br.com.ecommerce.customer.customer_service.entity.Address;
import br.com.ecommerce.customer.customer_service.entity.Customer;
import br.com.ecommerce.customer.customer_service.service.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> create(
            @RequestBody Customer customer) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.create(customer));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAll() {

        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(customerService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(
            @PathVariable Long id,
            @RequestBody Customer customer) {

        return ResponseEntity.ok(
                customerService.update(id, customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        customerService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<Address> addAddress(
            @PathVariable Long id,
            @RequestBody Address address) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.addAddress(id, address));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<Address>> findAddresses(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                customerService.findAddresses(id));
    }
}
