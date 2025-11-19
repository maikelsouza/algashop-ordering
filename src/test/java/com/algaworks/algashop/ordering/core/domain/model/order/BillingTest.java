package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.commons.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BillingTest {

    @Test
    void shouldNotCreateFullNameNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Billing(null,new Document("value"),
                        new Phone("000-000-0000"),new Email("John.peter@gmail.com"), Address.builder()
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
                .isThrownBy(() -> new Billing(new FullName("John", "Peter"),null,
                        new Phone("000-000-0000"), new Email("John.peter@gmail.com"), Address.builder()
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
                .isThrownBy(() -> new Billing(new FullName("John", "Peter"),new Document("valeu"),
                        null,new Email("John.peter@gmail.com"), Address.builder()
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
                .isThrownBy(() -> new Billing(new FullName("John", "Peter"),null,
                        new Phone("000-000-0000"), new Email("John.peter@gmail.com"),null));
    }

    @Test
    void shouldCreate(){
        var billingInfo = new Billing(new FullName("John", "Peter"),
                new Document("valeu"), new Phone("000-000-0000"),new Email("John.peter@gmail.com"),
                    Address.builder()
                            .street("Bourbon Street")
                            .number("1134")
                            .neighborhood("North Ville")
                            .city("New York")
                            .state("New York")
                            .zipCode(new ZipCode("12345"))
                            .complement("Apt 114")
                            .build());

        Assertions.assertThat(billingInfo.toString())
                .isEqualTo("John Peter valeu 000-000-0000 John.peter@gmail.com Bourbon Street Apt 114 North Ville 1134 New York New York 12345");


    }


}