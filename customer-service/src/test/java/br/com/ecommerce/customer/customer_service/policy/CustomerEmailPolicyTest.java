package br.com.ecommerce.customer.customer_service.policy;

import br.com.ecommerce.customer.customer_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerEmailPolicyTest {
    CustomerRepository repository = mock(CustomerRepository.class);
    CustomerEmailPolicy policy = new CustomerEmailPolicy(repository);

    @Test void acceptsNewEmail() {
        policy.validateForCreate("new@example.com");
        verify(repository).existsByEmail("new@example.com");
    }
    @Test void rejectsExistingEmail() {
        when(repository.existsByEmail("used@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> policy.validateForCreate("used@example.com"));
    }
    @Test void rejectsBlankAndNullOnCreate() {
        assertThrows(IllegalArgumentException.class, () -> policy.validateForCreate(" "));
        assertThrows(IllegalArgumentException.class, () -> policy.validateForCreate(null));
        verifyNoInteractions(repository);
    }
    @Test void acceptsUnchangedEmailWithoutRepositoryQuery() {
        policy.validateForUpdate("same@example.com", "same@example.com");
        verifyNoInteractions(repository);
    }
    @Test void acceptsNewAvailableEmailOnUpdate() {
        policy.validateForUpdate("old@example.com", "new@example.com");
        verify(repository).existsByEmail("new@example.com");
    }
    @Test void rejectsDuplicateEmailOnUpdate() {
        when(repository.existsByEmail("used@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> policy.validateForUpdate("old@example.com", "used@example.com"));
    }
    @Test void rejectsBlankAndNullOnUpdate() {
        assertThrows(IllegalArgumentException.class, () -> policy.validateForUpdate("old@example.com", " "));
        assertThrows(IllegalArgumentException.class, () -> policy.validateForUpdate("old@example.com", null));
        verifyNoInteractions(repository);
    }
}
