package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_FOUND;

public class ShoppingCartNotFoundException extends DomainException {


    public ShoppingCartNotFoundException(ShoppingCartId id) {
        super(String.format(ERROR_SHOPPING_CARD_FOUND,id));
    }
}

