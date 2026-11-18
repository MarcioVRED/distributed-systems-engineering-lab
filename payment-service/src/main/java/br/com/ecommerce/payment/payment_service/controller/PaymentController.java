package br.com.ecommerce.payment.payment_service.controller;
import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/payments") @RequiredArgsConstructor
public class PaymentController {
    private final PaymentQueryService queryService;
    @GetMapping public List<Payment> findAll() { return queryService.findAll(); }
    @GetMapping("/order/{orderId}") public Payment findByOrderId(@PathVariable Long orderId) { return queryService.findByOrderId(orderId); }
}
