package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_CUSTOMER_NOT_FOUND;

public class CustomerNotFoundException extends DomainException{


    public CustomerNotFoundException(CustomerId id) {
        super(String.format(ERROR_CUSTOMER_NOT_FOUND,id));
    }
}
