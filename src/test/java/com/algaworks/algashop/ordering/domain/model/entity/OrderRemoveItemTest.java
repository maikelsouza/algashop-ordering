package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED;
import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM;

public class OrderRemoveItemTest {

    @Test
    public void givenDraftOrder_whenRemoveItem_shouldAllowRemove() {
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();

        Product product = ProductTestDataBuilder.aProduct().build();
        Product productAltRamMemory = ProductTestDataBuilder.aProductAltRamMemory().build();
        order.addItem(product, new Quantity(1));
        order.addItem(productAltRamMemory, new Quantity(1));
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(2));

        OrderItem orderItem = order.items().iterator().next();
        order.removeItem(orderItem.id());
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(1));
    }


    @Test
    public void givenDraftOrder_whenRemoveItemNotExist_shouldGenerateException() {

        Order order = Order.draft(new CustomerId());
        OrderItemId orderItemId = new OrderItemId();

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(() -> order.removeItem(orderItemId))
                .withMessage(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM,order.id(), orderItemId));
    }

    @Test
    public void givenPaidOrder_whenRemoveItem_shouldGenerateException3() {

        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();

        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.removeItem(orderItem.id()))
                .withMessage(String.format(ERROR_ORDER_CANNOT_BE_EDITED, order.id(), order.status()));
    }
}

