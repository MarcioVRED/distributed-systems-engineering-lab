package br.com.ecommerce.order.order_service.consumer;

import br.com.ecommerce.order.order_service.service.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderConsumersTest {
    @Test
    void reserved() throws Exception {
        OrderStockService s = mock(OrderStockService.class);
        new StockReservedConsumer(s).consume("{\"orderId\":3}");
        verify(s).confirmStockReservation(3L);
    }

    @Test
    void rejected() throws Exception {
        OrderStockService s = mock(OrderStockService.class);
        new StockRejectedConsumer(s).consume("{\"orderId\":3,\"reason\":\"out\"}");
        verify(s).rejectStockReservation(3L, "out");
    }

    @Test
    void approved() throws Exception {
        OrderPaymentService s = mock(OrderPaymentService.class);
        new PaymentApprovedConsumer(s).consume("{\"orderId\":3}");
        verify(s).approvePayment(3L);
    }

    @Test
    void paymentRejected() throws Exception {
        OrderPaymentService s = mock(OrderPaymentService.class);
        new PaymentRejectedConsumer(s).consume("{\"orderId\":3,\"reason\":\"denied\"}");
        verify(s).rejectPayment(3L, "denied");
    }

    @Test
    void missingPaymentIds() {
        assertThrows(IllegalArgumentException.class, () -> new PaymentApprovedConsumer(mock(OrderPaymentService.class)).consume("{}"));
        assertThrows(IllegalArgumentException.class, () -> new PaymentRejectedConsumer(mock(OrderPaymentService.class)).consume("{}"));
    }

    @Test
    void malformedJson() {
        assertThrows(Exception.class, () -> new StockReservedConsumer(mock(OrderStockService.class)).consume("{"));
        assertThrows(Exception.class, () -> new StockRejectedConsumer(mock(OrderStockService.class)).consume("{"));
        assertThrows(Exception.class, () -> new PaymentApprovedConsumer(mock(OrderPaymentService.class)).consume("{"));
        assertThrows(Exception.class, () -> new PaymentRejectedConsumer(mock(OrderPaymentService.class)).consume("{"));
    }
}
