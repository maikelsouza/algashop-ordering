package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.utility.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerTest {

    @Test
    public void testingCustomer(){
        Customer customer = new Customer(
                IdGenerator.generateTimeBasedUUID(),
                "Jhon Doe",
                LocalDate.of(1991, 7, 5),
                "Jhon.doe@gmail.com",
                "478-256-2504",
                "255-08-0578",
                true,
                OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(10);
    }

}