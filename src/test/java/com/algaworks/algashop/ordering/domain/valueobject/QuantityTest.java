package com.algaworks.algashop.ordering.domain.valueobject;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

class QuantityTest {


    @Test
    void shouldNotCreatedValueNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Quantity(null))
                .withMessage(VALIDATION_ERROR_QUANTITY_IS_NULL);
    }

    @Test
    void shouldNotCreatedNegativeValue(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Quantity(-1))
                .withMessage(VALIDATION_ERROR_QUANTITY_IS_NEGATIVE);
    }

    @Test
    void shouldCreatedWithoutError(){
        var quantity = new Quantity(10);

        Assertions.assertThat(quantity.value()).isEqualTo(10);
    }

}