package com.algaworks.algashop.ordering.core.domain.model.product;

import com.algaworks.algashop.ordering.core.domain.model.DomainException;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_PRODUCT_NOT_FOUND;

public class ProductNotFoundException extends DomainException {

    public ProductNotFoundException() {}

    public ProductNotFoundException(ProductId id) {
        super(String.format(ERROR_PRODUCT_NOT_FOUND,id));
    }
}
