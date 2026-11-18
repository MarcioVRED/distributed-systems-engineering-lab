
package br.com.ecommerce.payment.payment_service.service;

import br.com.ecommerce.payment.payment_service.entity.Payment;
import br.com.ecommerce.payment.payment_service.entity.enums.PaymentStatus;
import br.com.ecommerce.payment.payment_service.event.PaymentProcessedCommittedEvent;
import br.com.ecommerce.payment.payment_service.event.PaymentRequestedEvent;
import br.com.ecommerce.payment.payment_service.policy.PaymentApprovalPolicy;
import br.com.ecommerce.payment.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepository repo;
    private PaymentApprovalPolicy policy;
    private ApplicationEventPublisher publisher;
    private PaymentService service;

    @BeforeEach
    void init() {
        repo = mock(PaymentRepository.class);
        policy = mock(PaymentApprovalPolicy.class);
        publisher = mock(ApplicationEventPublisher.class);

        service = new PaymentService(repo, policy, publisher);
    }

    @Test
    void approvesAndPublishes() {

        var request =
                new PaymentRequestedEvent(1L, BigDecimal.TEN);

        when(repo.findByOrderId(1L))
                .thenReturn(Optional.empty());

        when(policy.shouldApprove(BigDecimal.TEN))
                .thenReturn(true);

        when(repo.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        Payment p = service.process(request);

        assertEquals(PaymentStatus.APPROVED, p.getStatus());

        verify(publisher).publishEvent(
                new PaymentProcessedCommittedEvent(
                        1L,
                        PaymentStatus.APPROVED,
                        true
                )
        );
    }

    @Test
    void rejectsAndPublishes() {

        var amount = new BigDecimal("20000");

        when(repo.findByOrderId(2L))
                .thenReturn(Optional.empty());

        when(repo.save(any(Payment.class)))
                .thenAnswer(i -> i.getArgument(0));

        Payment p = service.process(
                new PaymentRequestedEvent(2L, amount)
        );

        assertEquals(PaymentStatus.REJECTED, p.getStatus());

        verify(publisher).publishEvent(
                new PaymentProcessedCommittedEvent(
                        2L,
                        PaymentStatus.REJECTED,
                        true
                )
        );
    }

    @Test
    void existingPaymentIsReusedAndEventReemitted() {

        Payment existing =
                new Payment(3L, BigDecimal.TEN);

        existing.approve();

        when(repo.findByOrderId(3L))
                .thenReturn(Optional.of(existing));

        assertSame(
                existing,
                service.process(
                        new PaymentRequestedEvent(
                                3L,
                                BigDecimal.TEN
                        )
                )
        );

        verify(repo, never()).save(any());

        verifyNoInteractions(policy);

        verify(publisher).publishEvent(
                new PaymentProcessedCommittedEvent(
                        3L,
                        PaymentStatus.APPROVED,
                        false
                )
        );
    }

    @Test
    void rejectsInvalidRequests() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(
                        new PaymentRequestedEvent(
                                null,
                                BigDecimal.ONE
                        )
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(
                        new PaymentRequestedEvent(
                                1L,
                                null
                        )
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(
                        new PaymentRequestedEvent(
                                1L,
                                BigDecimal.ZERO
                        )
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.process(
                        new PaymentRequestedEvent(
                                1L,
                                BigDecimal.ONE.negate()
                        )
                )
        );

        verifyNoInteractions(repo, policy, publisher);
    }
}
