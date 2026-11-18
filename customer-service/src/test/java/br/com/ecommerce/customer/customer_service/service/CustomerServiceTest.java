package br.com.ecommerce.customer.customer_service.service;

import br.com.ecommerce.customer.customer_service.entity.*;
import br.com.ecommerce.customer.customer_service.policy.CustomerEmailPolicy;
import br.com.ecommerce.customer.customer_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock CustomerRepository customers;
    @Mock AddressRepository addresses;
    @Mock CustomerEmailPolicy emailPolicy;
    CustomerService service;
    Customer customer;

    @BeforeEach void setup() {
        service = new CustomerService(customers, addresses, emailPolicy);
        customer = new Customer("Ana", "ana@example.com");
        customer.setId(1L);
    }

    @Test void createsCustomer() {
        when(customers.save(customer)).thenReturn(customer);
        assertSame(customer, service.create(customer));
        verify(emailPolicy).validateForCreate("ana@example.com");
    }

    @Test void doesNotSaveIfEmailPolicyRejects() {
        doThrow(new IllegalArgumentException("duplicate")).when(emailPolicy).validateForCreate("ana@example.com");
        assertThrows(IllegalArgumentException.class, () -> service.create(customer));
        verifyNoInteractions(customers);
    }

    @Test void listsCustomers() {
        when(customers.findAll()).thenReturn(List.of(customer));
        assertEquals(List.of(customer), service.findAll());
    }

    @Test void findsCustomer() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        assertSame(customer, service.findById(1L));
    }

    @Test void throwsWhenCustomerMissing() {
        when(customers.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findById(9L));
    }

    @Test void addsAddressAndLinksCustomer() {
        Address address = new Address("Rua A", "Rio", "RJ", "12345");
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(customers.save(customer)).thenReturn(customer);
        assertSame(address, service.addAddress(1L, address));
        assertSame(customer, address.getCustomer());
        assertEquals(1, customer.getAddresses().size());
    }

    @Test void addAddressFailsIfCustomerMissing() {
        when(customers.findById(8L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.addAddress(8L, new Address()));
        verify(customers, never()).save(any());
    }

    @Test void listsAddresses() {
        Address address = new Address("Rua A", "Rio", "RJ", "12345");
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(addresses.findByCustomerId(1L)).thenReturn(List.of(address));
        assertEquals(List.of(address), service.findAddresses(1L));
    }

    @Test void doesNotListAddressesForMissingCustomer() {
        when(customers.findById(8L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findAddresses(8L));
        verifyNoInteractions(addresses);
    }

    @Test void updatesCustomer() {
        Customer changed = new Customer("Bia", "bia@example.com");
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        when(customers.save(customer)).thenReturn(customer);
        assertSame(customer, service.update(1L, changed));
        assertEquals("Bia", customer.getName());
        assertEquals("bia@example.com", customer.getEmail());
        verify(emailPolicy).validateForUpdate("ana@example.com", "bia@example.com");
    }

    @Test void doesNotMutateCustomerWhenUpdateRejected() {
        Customer changed = new Customer("Bia", "taken@example.com");
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        doThrow(new IllegalArgumentException("duplicate")).when(emailPolicy)
                .validateForUpdate("ana@example.com", "taken@example.com");
        assertThrows(IllegalArgumentException.class, () -> service.update(1L, changed));
        assertEquals("Ana", customer.getName());
        verify(customers, never()).save(any());
    }

    @Test void deletesCustomer() {
        when(customers.findById(1L)).thenReturn(Optional.of(customer));
        service.delete(1L);
        verify(customers).delete(customer);
    }

    @Test void cannotDeleteMissingCustomer() {
        when(customers.findById(9L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.delete(9L));
        verify(customers, never()).delete(any());
    }
}
