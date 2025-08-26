package com.algaworks.algashop.ordering.domain.model.exception;

import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CARD_ITEM_INCOMPATIBLE_PRODUCT;

public class ShoppingCartItemIncompatibleProductException extends DomainException{

    public ShoppingCartItemIncompatibleProductException(ProductId productId, ProductId id) {
        super(String.format(ERROR_SHOPPING_CARD_ITEM_INCOMPATIBLE_PRODUCT, productId, id));
    }
}
