package com.algaworks.algashop.ordering.domain.valueobject;

public record Address(String street,
                      String complement,
                      String neighborhood,
                      String city,
                      String state,
                      ZipCode zipCode) {
}
