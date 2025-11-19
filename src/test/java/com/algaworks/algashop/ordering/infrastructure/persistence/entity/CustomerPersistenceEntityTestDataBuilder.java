package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class CustomerPersistenceEntityTestDataBuilder {

    private CustomerPersistenceEntityTestDataBuilder() {
    }

    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
                .email("john.doe@gmail.com")
                .phone("000-000-0000")
                .archived(false)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .birthDate(LocalDate.now())
                .document("000-00-0000")
                .firstName("john")
                .loyaltyPoints(10)
                .address(buildAddressEmbeddable())
                .promotionNotificationsAllowed(true)
                .lastName("doe");

    }

    private static AddressEmbeddable buildAddressEmbeddable() {
        return AddressEmbeddable.builder()
                .street("Bourbon Street")
                .number("1134")
                .neighborhood("North Ville")
                .city("New York")
                .state("New York")
                .zipCode("12345")
                .complement("Apt 114")
                .build();
    }
}
