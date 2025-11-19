package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.DomainEntityNotFoundException;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM;

public class OrderDoesNotContainOrderItemException extends DomainEntityNotFoundException {

    public OrderDoesNotContainOrderItemException(OrderId id, OrderItemId orderItemId) {
        super(String.format(ERROR_ORDER_DOES_NOT_CONTAIN_ITEM,id, orderItemId));
    }
}
