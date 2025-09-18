package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_NOT_FOUND;

public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(OrderId id) {
        super(String.format(ERROR_ORDER_NOT_FOUND,id));
    }
}
