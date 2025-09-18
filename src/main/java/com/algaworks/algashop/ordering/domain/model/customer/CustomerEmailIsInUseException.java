package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE;

public class CustomerEmailIsInUseException extends DomainException {

    public CustomerEmailIsInUseException(CustomerId customerId) {
        super(String.format(ERROR_CUSTOMER_EMAIL_IS_IN_USE,customerId));
    }

}
