package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_NOT_FOUND;
import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_NOT_FOUND_FOR_CUSTOMER;

public class ShoppingCartNotFoundException extends DomainEntityNotFoundException {




    public ShoppingCartNotFoundException(ShoppingCartId id) {
        super(String.format(ERROR_SHOPPING_CARD_NOT_FOUND,id));
    }

    public ShoppingCartNotFoundException(CustomerId customerId) {
        super(String.format(ERROR_SHOPPING_CARD_NOT_FOUND_FOR_CUSTOMER,customerId));
    }


}

