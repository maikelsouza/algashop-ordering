package com.algaworks.algashop.ordering.domain.model.valueobject;



import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.*;


class FullNameTest {

    @Test
    void shouldNoCreateFirstNameNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new FullName(null, "name"))
                .withMessage(VALIDATION_ERROR_FULLNAME_FIRST_NAME_IS_NULL);
    }

    @Test
    void shouldNoCreateLastNameNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new FullName("name", null))
                .withMessage(VALIDATION_ERROR_FULLNAME_LAST_NAME_IS_NULL);
    }

    @Test
    void shouldNoCreateFirstNameBlank(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new FullName("", "name"))
                .withMessage(VALIDATION_ERROR_FULLNAME_FIRST_NAME_IS_BLANK);
    }

    @Test
    void shouldNoCreateLastNameBlank(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new FullName("name", ""))
                .withMessage(VALIDATION_ERROR_FULLNAME_LAST_NAME_IS_BLANK);
    }

    @Test
    void shoulDCreateFullName(){
        var fullName = new FullName("John", "Peter");

        Assertions.assertThat(fullName.toString()).isEqualTo("John Peter");

    }



}