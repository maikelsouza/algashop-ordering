package com.algaworks.algashop.ordering.domain.valueobject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShippingInfoTest {

    @Test
    void shouldNotCreateFullNameNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ShippingInfo(null,new Document("value"),
                        new Phone("000-000-0000"), Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("New York")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt 114")
                        .build()));
    }

    @Test
    void shouldNotCreateDocumentNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ShippingInfo(new FullName("John", "Peter"),null,
                        new Phone("000-000-0000"), Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("New York")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt 114")
                        .build()));
    }

    @Test
    void shouldNotCreatePhoneNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ShippingInfo(new FullName("John", "Peter"),new Document("valeu"),
                        null, Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("New York")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt 114")
                        .build()));
    }

    @Test
    void shouldNotCreateAddressNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new ShippingInfo(new FullName("John", "Peter"),null,
                        new Phone("000-000-0000"), null));
    }

    @Test
    void shouldCreate(){
        var shippingInfo = new ShippingInfo(new FullName("John", "Peter"),
                new Document("valeu"), new Phone("000-000-0000"),
                Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("New York")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt 114")
                        .build());

        Assertions.assertThat(shippingInfo.toString())
                .isEqualTo("John Peter valeu 000-000-0000 Bourbon Street Apt 114 North Ville 1134 New York New York 12345");

    }

}