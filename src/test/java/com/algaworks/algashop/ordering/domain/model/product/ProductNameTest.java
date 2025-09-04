package com.algaworks.algashop.ordering.domain.model.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.VALIDATION_ERROR_PRODUCT_NAME_IS_BLANK;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.VALIDATION_ERROR_PRODUCT_NAME_IS_NULL;

class ProductNameTest {


    @Test
    void shouldNotCreateNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ProductName(null))
                .withMessage(VALIDATION_ERROR_PRODUCT_NAME_IS_NULL);
    }

    @Test
    void shouldNotCreateBlank(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new ProductName(""))
                .withMessage(VALIDATION_ERROR_PRODUCT_NAME_IS_BLANK);
    }

    @Test
    void shouldCreate(){
        String value = "value";
        var productName = new ProductName("value");

        Assertions.assertThat(productName.toString()).isEqualTo(value);
    }

}