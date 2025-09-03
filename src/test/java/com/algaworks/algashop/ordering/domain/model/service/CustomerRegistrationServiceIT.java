package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class CustomerRegistrationServiceIT {

    @Autowired
    private CustomerRegistrationService customerRegistrationService;


    @Test
    public void shouldRegister(){
        Customer customer = customerRegistrationService.register(
                new FullName("Jhon","Doe"),
                new BirthDate(LocalDate.of(1991, 7, 5)),
                new Email("john.doe@gmail.com"),
                new Phone("478-256-2504"),
                new Document("255-08-0578"),
                true,
                Address.builder()
                        .street("Bourbon Street")
                        .number("1134")
                        .neighborhood("North Ville")
                        .city("New York")
                        .state("New York")
                        .zipCode(new ZipCode("12345"))
                        .complement("Apt 114")
                        .build()
                );

        Assertions.assertThat(customer.fullName()).isEqualTo(new FullName("Jhon","Doe"));
        Assertions.assertThat(customer.email()).isEqualTo(new Email("john.doe@gmail.com"));
        Assertions.assertThat(customer.birthDate()).isEqualTo(new BirthDate(LocalDate.of(1991, 7, 5)));
        Assertions.assertThat(customer.phone()).isEqualTo( new Phone("478-256-2504"));
        Assertions.assertThat(customer.document()).isEqualTo(new Document("255-08-0578"));

    }




}