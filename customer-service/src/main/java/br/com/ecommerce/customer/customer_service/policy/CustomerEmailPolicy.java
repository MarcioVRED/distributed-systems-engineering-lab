package br.com.ecommerce.customer.customer_service.policy;

import br.com.ecommerce.customer.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerEmailPolicy {
    private final CustomerRepository customerRepository;

    public void validateForCreate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
    }

    public void validateForUpdate(String currentEmail, String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (!newEmail.equals(currentEmail) && customerRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }
    }
}
