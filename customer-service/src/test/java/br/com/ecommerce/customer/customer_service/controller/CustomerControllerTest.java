package br.com.ecommerce.customer.customer_service.controller;

import br.com.ecommerce.customer.customer_service.entity.*;
import br.com.ecommerce.customer.customer_service.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {
    CustomerService service = mock(CustomerService.class);
    CustomerController controller = new CustomerController(service);
    Customer customer = new Customer("Ana", "ana@example.com");

    @Test void creates() {
        when(service.create(customer)).thenReturn(customer);
        var response = controller.create(customer);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(customer, response.getBody());
    }
    @Test void findsAll() {
        when(service.findAll()).thenReturn(List.of(customer));
        assertEquals(List.of(customer), controller.findAll().getBody());
    }
    @Test void findsById() {
        when(service.findById(1L)).thenReturn(customer);
        assertSame(customer, controller.findById(1L).getBody());
    }
    @Test void updates() {
        when(service.update(1L, customer)).thenReturn(customer);
        assertSame(customer, controller.update(1L, customer).getBody());
    }
    @Test void deletes() {
        assertEquals(HttpStatus.NO_CONTENT, controller.delete(1L).getStatusCode());
        verify(service).delete(1L);
    }
    @Test void addsAddress() {
        Address address = new Address("Rua", "Rio", "RJ", "12345");
        when(service.addAddress(1L, address)).thenReturn(address);
        var response = controller.addAddress(1L, address);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(address, response.getBody());
    }
    @Test void listsAddresses() {
        Address address = new Address("Rua", "Rio", "RJ", "12345");
        when(service.findAddresses(1L)).thenReturn(List.of(address));
        assertEquals(List.of(address), controller.findAddresses(1L).getBody());
    }
}
