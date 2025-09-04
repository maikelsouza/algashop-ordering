package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART;

public class CustomerAlreadyHaveShoppingCartException extends DomainException{

    public CustomerAlreadyHaveShoppingCartException(CustomerId customerId) {
        super(String.format(ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART,customerId));
    }
}
