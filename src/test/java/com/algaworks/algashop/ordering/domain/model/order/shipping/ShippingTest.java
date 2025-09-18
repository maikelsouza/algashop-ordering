package com.algaworks.algashop.ordering.domain.model.order.shipping;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ShippingTest {

    @Test
    void shouldNotCreateCostNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Shipping(null, LocalDate.now(),
                        Recipient.builder()
                                .fullName(new FullName("John", "Doe"))
                                .document(new Document("112-33-2321"))
                                .phone(new Phone("111-441-1244"))
                                .build(), Address.builder()
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
    void shouldNotCreateExpectedDateNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Shipping(new Money("10"),null,
                        Recipient.builder()
                                .fullName(new FullName("John", "Doe"))
                                .document(new Document("112-33-2321"))
                                .phone(new Phone("111-441-1244"))
                                .build(),
                        Address.builder()
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
    void shouldNotCreateRecipientNull(){
        Assertions.assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new Shipping(new Money("10"), LocalDate.now(),
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
                .isThrownBy(() -> new Shipping(new Money("100"),LocalDate.now(),
                        Recipient.builder()
                                .fullName(new FullName("John", "Doe"))
                                .document(new Document("112-33-2321"))
                                .phone(new Phone("111-441-1244"))
                                .build(),null));
    }

    @Test
    void shouldCreate(){
        var shipping = new Shipping(new Money("10"),
                LocalDate.of(2025,8,21),  Recipient.builder()
                .fullName(new FullName("John", "Doe"))
                .document(new Document("112-33-2321"))
                .phone(new Phone("111-441-1244"))
                .build(),
                Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("New York")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt 114")
                        .build());

        Assertions.assertThat(shipping.toString())
                .isEqualTo("10.00 2025-08-21 John Doe 112-33-2321 111-441-1244 Bourbon Street Apt 114 North Ville 1134 New York New York 12345");

    }

}