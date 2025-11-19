package com.algaworks.algashop.ordering.core.domain.model.customer;

import com.algaworks.algashop.ordering.core.domain.model.DomainException;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE;

public class CustomerEmailIsInUseException extends DomainException {

    public CustomerEmailIsInUseException(CustomerId customerId) {
        super(ERROR_CUSTOMER_EMAIL_IS_IN_USE);
    }

}
