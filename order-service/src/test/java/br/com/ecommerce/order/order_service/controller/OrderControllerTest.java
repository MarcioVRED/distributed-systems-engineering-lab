package br.com.ecommerce.order.order_service.controller;

import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {
    OrderService service = mock(OrderService.class);
    OrderController controller = new OrderController(service);

    @Test
    void creates() {
        Order o = new Order();
        when(service.create(o)).thenReturn(o);
        assertEquals(HttpStatus.CREATED, controller.create(o).getStatusCode());
        assertSame(o, controller.create(o).getBody());
    }

    @Test
    void lists() {
        when(service.findAll()).thenReturn(List.of(new Order()));
        assertEquals(1, controller.findAll().getBody().size());
    }

    @Test
    void getsById() {
        Order o = new Order();
        when(service.findById(1L)).thenReturn(o);
        assertSame(o, controller.findById(1L).getBody());
    }

    @Test
    void getsByCustomer() {
        when(service.findByCustomerId(2L)).thenReturn(List.of(new Order()));
        assertEquals(1, controller.findByCustomerId(2L).getBody().size());
    }

    @Test
    void cancels() {
        Order o = new Order();
        when(service.cancel(1L)).thenReturn(o);
        assertSame(o, controller.cancel(1L).getBody());
    }
}
