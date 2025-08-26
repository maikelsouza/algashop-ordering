package com.algaworks.algashop.ordering.domain.model.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class OrderIsCanceledTest {


    @Test
    public void givenCanceledOrder_shouldIsCanceled(){

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();

        Assertions.assertThat(order.isCanceled()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELED"})
    public void givenNotCanceledOrder_shouldIsNotCanceled(OrderStatus orderStatus){

        Order order = OrderTestDataBuilder.anOrder().status(orderStatus).build();

        Assertions.assertThat(order.isCanceled()).isFalse();
    }
}
