package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT;

public class ShoppingCartCantProceedToCheckoutException extends DomainException{

    public ShoppingCartCantProceedToCheckoutException(ShoppingCartId shoppingCartId) {
        super(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT,shoppingCartId));
    }
}
