package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_NOT_FOUND;

public class CustomerNotFoundException extends DomainEntityNotFoundException {


    public CustomerNotFoundException(CustomerId id) {
        super(String.format(ERROR_CUSTOMER_NOT_FOUND,id));
    }
}
