package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART;

public class CustomerAlreadyHaveShoppingCartException extends DomainException {

    public CustomerAlreadyHaveShoppingCartException(CustomerId customerId) {
        super(String.format(ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART,customerId));
    }
}
