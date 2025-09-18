package com.algaworks.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED;

public class OrderMarkAsReadyTest {


    @Test
    public void givenPaidOrder_whenMaskAsReady_shouldChangeToReady(){
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        order.markAsReady();
        Assertions.assertThat(order.isReady()).isTrue();
        Assertions.assertThat(order.readyAt()).isNotNull();
        Assertions.assertThat(order.paidAt()).isNotNull();
    }

    @Test
    public void givenPlacedOrder_whenMaskAsReady_shouldGenerateException(){
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::markAsReady)
                .withMessage(String.format(ERROR_ORDER_STATUS_CANNOT_BE_CHANGED,order.id(), OrderStatus.PLACED, OrderStatus.READY));
    }
}
