package com.algaworks.algashop.ordering.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

public record Money(BigDecimal value) implements Comparable<Money>{

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money(String value) {
        this(new BigDecimal(value));
    }

    public Money(BigDecimal value) {
        Objects.requireNonNull(value, VALIDATION_ERROR_MONEY_IS_NULL);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(VALIDATION_ERROR_MONEY_IS_NEGATIVE);
        }
        this.value = value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Money multiply(Quantity quantity){
        return new Money(value.multiply(BigDecimal.valueOf(quantity.value())));
    }

    public Money add(Money outer){
        return new Money(value.add(outer.value));
    }

    public Money divide(Money other) {
        Objects.requireNonNull(value, VALIDATION_ERROR_MONEY_IS_NULL);
        if (other.value.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException(VALIDATION_ERROR_MONEY_IS_DIVIDED_BY_ZERO);
        }
        return new Money(this.value.divide(other.value, 2, RoundingMode.HALF_EVEN));
    }

    @Override
    public int compareTo(Money other) {
        return this.value().compareTo(other.value());
    }


    @Override
    public String toString() {
        return value.toString();
    }


}
