package br.com.ecommerce.payment.payment_service.service;
import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service @RequiredArgsConstructor
public class PaymentQueryService {
    private final PaymentRepository paymentRepository;
    @Transactional(readOnly = true)
    public List<Payment> findAll() { return paymentRepository.findAll(); }
    @Transactional(readOnly = true)
    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment not found for order: " + orderId));
    }
}
