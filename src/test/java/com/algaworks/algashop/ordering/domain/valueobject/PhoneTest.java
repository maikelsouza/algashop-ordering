package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.VALIDATION_ERROR_PHONE_IS_BLANK;

class PhoneTest {


    @Test
    void shouldNotCreatePhoneNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Phone(null));
    }

    @Test
    void shouldNotCreatePhoneBlank(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Phone(""))
                .withMessage(VALIDATION_ERROR_PHONE_IS_BLANK);
    }

    @Test
    void shouldCreatePhoneNotNullAndNotBlank(){
        String value = "000-000-0000";
        Phone document = new Phone(value);

        Assertions.assertThat(document.value()).isEqualTo(value);
    }

    @Test
    void shouldReturnValuePhone(){
        String value = "000-000-0000";
        Phone phone = new Phone(value);

        Assertions.assertThat(phone.toString()).isEqualTo(value);
    }


}