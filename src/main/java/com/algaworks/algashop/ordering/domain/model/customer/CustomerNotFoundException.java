package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_NOT_FOUND;

public class CustomerNotFoundException extends DomainException {


    public CustomerNotFoundException(CustomerId id) {
        super(String.format(ERROR_CUSTOMER_NOT_FOUND,id));
    }

    public CustomerNotFoundException(UUID id) {
        super(String.format(ERROR_CUSTOMER_NOT_FOUND,id));
    }
}
