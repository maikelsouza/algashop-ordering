package com.algaworks.algashop.ordering.core.domain.model.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST;

class BirthDateTest {


    @Test
    void shouldNotCreateDateGreaterToday(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new BirthDate(LocalDate.now().plusDays(1)))
                .withMessage(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
    }

    @Test
    void shouldCreateDateEarlierToday(){
        BirthDate birthDate = new BirthDate(LocalDate.now().minusDays(1));
        Assertions.assertThat(birthDate.value()).isEqualTo(LocalDate.now().minusDays(1));
    }

    @Test
    void shouldNotAddCreateDateNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new BirthDate(null));
    }

    @Test
    void shouldCalculateAgeInYears(){
        int yearsToSubtract = 25;
        LocalDate todayMinus25Years = LocalDate.now().minusYears(yearsToSubtract);
        BirthDate birthDate = new BirthDate(todayMinus25Years);
        Assertions.assertThat(birthDate.age()).isEqualTo(yearsToSubtract);
    }

    @Test
    void shouldReturnDateStringFormat(){
        BirthDate birthDate = new BirthDate(LocalDate.of(2025, 8,1));
        Assertions.assertThat(birthDate.toString()).isEqualTo("2025-08-01");
    }


}