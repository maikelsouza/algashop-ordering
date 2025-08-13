package com.algaworks.algashop.ordering.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

public record Quantity(Integer value) implements Comparable<Quantity>{

    public static final Quantity ZERO = new Quantity(0);

    public Quantity(Integer value) {
        Objects.requireNonNull(Objects.requireNonNull(value, VALIDATION_ERROR_QUANTITY_IS_NULL));
        if (value < 0){
            throw  new IllegalArgumentException(VALIDATION_ERROR_QUANTIDY_IS_NEGATIVE);
        }
        this.value = value;
    }

    public Quantity add(Quantity value){
        return new Quantity(this.value + value.value);
    }


    @Override
    public int compareTo(Quantity outher) {
        return this.value().compareTo(outher.value());
    }

    @Override
    public String toString() {
                return value.toString();
    }
}
