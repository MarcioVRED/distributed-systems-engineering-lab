package br.com.ecommerce.customer.customer_service.controller;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.http.HttpStatus;

class CustomerExceptionHandlerTest {
    CustomerExceptionHandler handler = new CustomerExceptionHandler();
    @Test void returnsNotFound() {
        var response = handler.notFound(new NoSuchElementException("missing"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("missing", response.getBody().get("error"));
    }
    @Test void returnsBadRequest() {
        var response = handler.badRequest(new IllegalArgumentException("invalid"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid", response.getBody().get("error"));
    }
}
