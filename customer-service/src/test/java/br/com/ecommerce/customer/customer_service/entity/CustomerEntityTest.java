package br.com.ecommerce.customer.customer_service.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerEntityTest {
    @Test void customerGettersSettersAndConstructors() {
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setName("Ana");
        customer.setEmail("ana@example.com");
        assertEquals(3L, customer.getId());
        assertEquals("Ana", customer.getName());
        assertEquals("ana@example.com", customer.getEmail());
        assertTrue(customer.getAddresses().isEmpty());
        assertEquals("Bia", new Customer("Bia", "bia@example.com").getName());
    }
    @Test void addressGettersSettersAndConstructors() {
        Address address = new Address();
        address.setId(2L);
        address.setStreet("Rua");
        address.setCity("Rio");
        address.setState("RJ");
        address.setZipCode("12345");
        assertEquals(2L, address.getId());
        assertEquals("Rua", address.getStreet());
        assertEquals("Rio", address.getCity());
        assertEquals("RJ", address.getState());
        assertEquals("12345", address.getZipCode());
        assertEquals("Outra", new Address("Outra", "SP", "SP", "45678").getStreet());
    }
    @Test void addAndRemoveAddressKeepBothSidesConsistent() {
        Customer customer = new Customer("Ana", "ana@example.com");
        Address address = new Address("Rua", "Rio", "RJ", "12345");
        customer.addAddress(address);
        customer.addAddress(address);
        assertEquals(1, customer.getAddresses().size());
        assertSame(customer, address.getCustomer());
        customer.removeAddress(address);
        assertTrue(customer.getAddresses().isEmpty());
        assertNull(address.getCustomer());
    }
    @Test void removingMissingOrNullAddressDoesNothing() {
        Customer customer = new Customer();
        Address address = new Address();
        customer.removeAddress(address);
        customer.removeAddress(null);
        assertTrue(customer.getAddresses().isEmpty());
    }
    @Test void addingNullAddressIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Customer().addAddress(null));
    }
}
