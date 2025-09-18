package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.DomainException;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_PRODUCT_NOT_FOUND;

public class ProductNotFoundException extends DomainException {


    public ProductNotFoundException(ProductId id) {
        super(String.format(ERROR_PRODUCT_NOT_FOUND,id));
    }
}
