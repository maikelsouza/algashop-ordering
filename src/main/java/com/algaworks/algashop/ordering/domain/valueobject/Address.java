package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.validator.FieldValidations;
import lombok.Builder;

import java.util.Objects;


public record Address(String street,
                      String complement,
                      String neighborhood,
                      String number,
                      String city,
                      String state,
                      ZipCode zipCode) {


    @Builder(toBuilder = true)
    public Address {
        FieldValidations.requiresNonBlanks(street);
        FieldValidations.requiresNonBlanks(neighborhood);
        FieldValidations.requiresNonBlanks(number);
        FieldValidations.requiresNonBlanks(city);
        FieldValidations.requiresNonBlanks(state);
        Objects.requireNonNull(zipCode);
    }

    @Override
    public String toString() {
        return  street + " " + complement + " " + neighborhood + " " + number + " " + city + " " + state + " " + zipCode;
    }
}
