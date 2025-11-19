package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.DomainException;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {

    public ShoppingCartCantProceedToCheckoutException(ShoppingCartId shoppingCartId) {
        super(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT,shoppingCartId));
    }
}
