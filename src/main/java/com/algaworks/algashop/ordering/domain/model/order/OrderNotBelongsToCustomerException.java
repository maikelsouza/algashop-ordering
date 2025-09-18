package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER;

public class OrderNotBelongsToCustomerException extends DomainException {


    public OrderNotBelongsToCustomerException(OrderId id, CustomerId customerId) {
        super(String.format(ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER, id, customerId));
    }

}


