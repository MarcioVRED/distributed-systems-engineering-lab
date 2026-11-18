
package br.com.ecommerce.payment.payment_service.service;

import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.event.PaymentProcessedCommittedEvent;
import br.com.ecommerce.payment.payment_service.event.PaymentRequestedEvent;
import br.com.ecommerce.payment.payment_service.repository.PaymentRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import br.com.ecommerce.payment.payment_service.policy.PaymentApprovalPolicy;

@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentApprovalPolicy approvalPolicy;
    private final ApplicationEventPublisher applicationEventPublisher;


    @Transactional
    public Payment process(PaymentRequestedEvent event) {

        // 1. Validar a solicitação de pagamento
        if (event == null ||
                event.orderId() == null ||
                event.amount() == null ||
                event.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Invalid payment request"
            );
        }

        // 2. Verificar se o pagamento já foi processado
        var existing =
                paymentRepository.findByOrderId(event.orderId());

        if (existing.isPresent()) {

            Payment payment = existing.get();

            log.info("Payment already processed for order: {} | Status: {}", event.orderId(), payment.getStatus());

            // Republicar o resultado para permitir
            // recuperação em caso de reprocessamento Kafka
            applicationEventPublisher.publishEvent(
                    new PaymentProcessedCommittedEvent(
                            payment.getOrderId(),
                            payment.getStatus(),
                            false
                    )
            );

            return payment;
        }

        // 3. Criar o pagamento
        Payment payment = new Payment(
                event.orderId(),
                event.amount()
        );

        // 4. Aplicar a regra simulada de aprovação
        if (approvalPolicy.shouldApprove(event.amount())) {

            payment.approve();

        } else {

            payment.reject();
        }

        // 5. Persistir no banco H2
        Payment saved = paymentRepository.save(payment);

        log.info("Payment processed. Order: {} | Status: {}", saved.getOrderId(), saved.getStatus());

        // 6. Publicar evento interno do Spring
        // O listener AFTER_COMMIT enviará ao Kafka
        applicationEventPublisher.publishEvent(
                new PaymentProcessedCommittedEvent(
                        saved.getOrderId(),
                        saved.getStatus(),
                        true
                )
        );

        return saved;
    }
}
