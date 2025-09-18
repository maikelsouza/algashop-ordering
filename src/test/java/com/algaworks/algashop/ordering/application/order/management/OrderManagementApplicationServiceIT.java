package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.application.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_NOT_FOUND;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

@SpringBootTest
@Transactional
@Import(OrderEventListener.class)
class OrderManagementApplicationServiceIT {

    @Autowired
    private OrderManagementApplicationService orderManagementApplicationService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @MockitoSpyBean
    private CustomerLoyaltyPointsApplicationService loyaltyPointsApplicationService;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    public void givenAnOrder_whenCancel_shouldSuccessfully(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .build();
        orders.add(order);
        orderManagementApplicationService.cancel(order.id().toString());

        Order cancelOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(cancelOrder.canceledAt()).isNotNull();
        Assertions.assertThat(cancelOrder.isCanceled()).isTrue();
        Mockito.verify(orderEventListener).listen(Mockito.any(OrderCanceledEvent.class));
    }

    @Test
    public void givenAnOrder_whenTryCancelWithOrderNotFound_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .build();

        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() ->orderManagementApplicationService.cancel(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_NOT_FOUND,order.id()));
    }

    @Test
    public void givenAnOrder_whenTryCancelWithCanceledStatus_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.CANCELED)
                .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() ->orderManagementApplicationService.cancel(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), order.status(), OrderStatus.CANCELED));
    }

    @Test
    public void givenAnOrder_whenMarkAsPaid_shouldSuccessfully(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.PLACED)
                .build();
        orders.add(order);
        orderManagementApplicationService.markAsPaid(order.id().toString());

        Order cancelOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(cancelOrder.paidAt()).isNotNull();
        Assertions.assertThat(cancelOrder.isPaid()).isTrue();
        Mockito.verify(orderEventListener).listen(Mockito.any(OrderPaidEvent.class));
    }

    @Test
    public void givenAnOrder_whenTryMarkAsPaidWithOrderNotFound_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .build();

        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() ->orderManagementApplicationService.markAsPaid(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_NOT_FOUND,order.id()));
    }

    @Test
    public void givenAnOrder_whenTryCancelWithMarkAsPaidStatus_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.PAID)
                .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() ->orderManagementApplicationService.markAsPaid(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), order.status(), OrderStatus.PAID));
    }

    @Test
    public void givenAnOrder_whenTryCancelWithMarkAsCanceledStatus_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.CANCELED)
                .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() ->orderManagementApplicationService.markAsPaid(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), order.status(), OrderStatus.PAID));
    }

    @Test
    public void givenAnOrder_whenMarkAsReady_shouldSuccessfully(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.PAID)
                .build();
        orders.add(order);
        orderManagementApplicationService.markAsReady(order.id().toString());

        Order cancelOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(cancelOrder.readyAt()).isNotNull();
        Assertions.assertThat(cancelOrder.isReady()).isTrue();
        Mockito.verify(orderEventListener).listen(Mockito.any(OrderReadyEvent.class));
        Mockito.verify(loyaltyPointsApplicationService).addLoyaltyPoints(Mockito.any(UUID.class)
                ,Mockito.any(String.class));
    }

    @Test
    public void givenAnOrder_whenTryMarkAsReadyWithOrderNotFound_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .build();

        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() ->orderManagementApplicationService.markAsReady(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_NOT_FOUND,order.id()));
    }

    @Test
    public void givenAnOrder_whenTryMarkAsReadyWithReadyStatus_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.READY)
                .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() ->orderManagementApplicationService.markAsReady(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), order.status(), OrderStatus.READY));
    }

    @Test
    public void givenAnOrder_whenTryMarkReadyWithPlacedStatus_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.PLACED)
                .build();
        orders.add(order);

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(() ->orderManagementApplicationService.markAsReady(order.id().toString()))
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), order.status(), OrderStatus.READY));
    }

}