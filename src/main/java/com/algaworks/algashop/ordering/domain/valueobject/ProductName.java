package com.algaworks.algashop.ordering.domain.valueobject;

import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

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
