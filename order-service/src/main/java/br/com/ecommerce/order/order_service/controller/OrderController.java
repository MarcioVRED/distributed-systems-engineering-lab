package br.com.ecommerce.order.order_service.controller;

import lombok.RequiredArgsConstructor;
import br.com.ecommerce.order.order_service.entity.Order;
import br.com.ecommerce.order.order_service.service.OrderService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {

        Order created = orderService.create(order);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {

        return ResponseEntity.ok(
                orderService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> findById(@PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.findById(id)
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> findByCustomerId(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                orderService.findByCustomerId(customerId)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Order> cancel(@PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancel(id)
        );
    }
}