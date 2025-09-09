package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_NOT_FOUND;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

@SpringBootTest
@Transactional
class OrderManagementApplicationServiceIT {

    @Autowired
    private OrderManagementApplicationService orderManagementApplicationService;

    @Autowired
    private Orders orders;

    @Autowired
    private Customers customers;

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
        orderManagementApplicationService.cancel(order.id().value().toLong());

        Order cancelOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(cancelOrder.canceledAt()).isNotNull();
        Assertions.assertThat(cancelOrder.isCanceled()).isTrue();
    }

    @Test
    public void givenAnOrder_whenTryCancelWithOrderNotFound_shouldGenerationException(){
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .build();

        Assertions.assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() ->orderManagementApplicationService.cancel(order.id().value().toLong()))
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
                .isThrownBy(() ->orderManagementApplicationService.cancel(order.id().value().toLong()))
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), order.status(), OrderStatus.CANCELED));
    }

}