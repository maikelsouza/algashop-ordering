package com.algaworks.algashop.ordering.domain.model.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.VALIDATION_ERROR_EMAIL_IS_INVALID;

class EmailTest {

    @Test
    void shouldNotCreateInvalidEmail(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Email("john.com.br"))
                .withMessage(VALIDATION_ERROR_EMAIL_IS_INVALID);
    }

    @Test
    void shouldCreateValidEmail(){
        String value = "john@gmail.com";
        Email email = new Email(value);

        Assertions.assertThat(email.value()).isEqualTo(value);
    }

    @Test
    void shouldReturnNameEmail(){
        String value = "john@gmail.com";
        Email email = new Email(value);

        Assertions.assertThat(email.toString()).isEqualTo(value);
    }


}