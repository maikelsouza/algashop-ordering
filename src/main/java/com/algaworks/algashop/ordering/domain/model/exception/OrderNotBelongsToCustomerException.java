package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER;

public class OrderNotBelongsToCustomerException extends DomainException {


    public OrderNotBelongsToCustomerException(OrderId id, CustomerId customerId) {
        super(String.format(ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER, id, customerId));
    }

}


