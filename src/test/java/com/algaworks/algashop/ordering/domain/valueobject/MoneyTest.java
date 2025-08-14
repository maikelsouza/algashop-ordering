package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.algaworks.algashop.ordering.domain.exception.ErrorMessages.*;

class MoneyTest {


    @Test
    void shouldNotCreateValeuNull(){
        BigDecimal bigDecimal = null;
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Money(bigDecimal))
                .withMessage(VALIDATION_ERROR_MONEY_IS_NULL);
    }

    @Test
    void shouldNotCreateValeuNegative(){
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Money("-1"))
                .withMessage(VALIDATION_ERROR_MONEY_IS_NEGATIVE);
    }

    @Test
    void shouldCreateValeuPositive(){
        BigDecimal valeu = BigDecimal.TEN;
        var money = new Money(valeu);
        Assertions.assertThat(money.value()).isEqualTo(valeu.setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldMultiplyForTwo(){
        BigDecimal valeu = BigDecimal.valueOf(20);
        var money = new Money(BigDecimal.TEN);
        Money result = money.multiply(new Quantity(2));

        Assertions.assertThat(result.value()).isEqualTo(valeu.setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldSumTwoValues(){
        BigDecimal valeu = BigDecimal.valueOf(20);
        var money = new Money(BigDecimal.TEN);
        var moneyOuter = new Money(BigDecimal.TEN);
        Money result = money.add(moneyOuter);

        Assertions.assertThat(result.value()).isEqualTo(valeu.setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void shouldNoDivideByZero(){
        var money = new Money(BigDecimal.TEN);
        var moneyOuter = new Money(BigDecimal.ZERO);

        Assertions.assertThatExceptionOfType(ArithmeticException.class)
                .isThrownBy( () -> money.divide(moneyOuter))
                .withMessage(VALIDATION_ERROR_MONEY_IS_DIVIDED_BY_ZERO);
    }

    @Test
    void shouldDivideByTwo(){
        var value = new Money(BigDecimal.valueOf(5));
        var money = new Money(BigDecimal.TEN);
        var moneyOuter = new Money(BigDecimal.TWO);

        Assertions.assertThat(money.divide(moneyOuter)).isEqualTo(value);
    }

    @Test
    void shouldOneMustBeGreaterThanZero(){
        var moneyOne = new Money(BigDecimal.ONE);
        var moneyZero = new Money(BigDecimal.ZERO);

        Assertions.assertThat(moneyOne.compareTo(moneyZero)).isEqualTo(1);
    }

    @Test
    void shouldZeroMustBeLessThanOne(){
        var moneyOne = new Money(BigDecimal.ONE);
        var moneyZero = new Money(BigDecimal.ZERO);

        Assertions.assertThat(moneyZero.compareTo(moneyOne)).isEqualTo(-1);
    }

    @Test
    void shouldOneMustBeEqualToOne(){
        var moneyOne = new Money(BigDecimal.ONE);
        var moneyOne2 = new Money(BigDecimal.ONE);

        Assertions.assertThat(moneyOne.compareTo(moneyOne2)).isEqualTo(0);
    }



}