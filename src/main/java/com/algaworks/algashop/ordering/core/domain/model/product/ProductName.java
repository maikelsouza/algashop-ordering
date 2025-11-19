package com.algaworks.algashop.ordering.core.domain.model.product;

import java.util.Objects;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.VALIDATION_ERROR_PRODUCT_NAME_IS_BLANK;
import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.VALIDATION_ERROR_PRODUCT_NAME_IS_NULL;

public record ProductName(String value) {

    public ProductName(String value) {
        Objects.requireNonNull(value, VALIDATION_ERROR_PRODUCT_NAME_IS_NULL);
        if (value.isBlank()){
            throw new IllegalArgumentException(VALIDATION_ERROR_PRODUCT_NAME_IS_BLANK);
        }
        this.value = value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}
