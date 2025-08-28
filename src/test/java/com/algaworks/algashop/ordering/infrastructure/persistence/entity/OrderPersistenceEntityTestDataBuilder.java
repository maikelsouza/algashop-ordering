package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class OrderPersistenceEntityTestDataBuilder {

    private OrderPersistenceEntityTestDataBuilder() {
    }


    public static OrderPersistenceEntity.OrderPersistenceEntityBuilder existingOrder() {
        return OrderPersistenceEntity.builder()
                .id(IdGenerator.generateTSID().toLong())
                .customerId(IdGenerator.generateTimeBasedUUID())
                .totalItems(2)
                .totalAmount(new BigDecimal(1000))
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .shipping(buildShippingEmbeddable())
                .billing(buildBillingEmbeddable())
                .placedAt(OffsetDateTime.now());
    }

    private static ShippingEmbeddable buildShippingEmbeddable() {
        return ShippingEmbeddable.builder()
                .cost(BigDecimal.TEN)
                .expectedDate(LocalDate.now())
                .address(buildAddressEmbeddable())
                .recipient(buildRecipientEmbeddable())
                .build();
    }

    private static BillingEmbeddable buildBillingEmbeddable() {
        return BillingEmbeddable.builder()
                .phone("111-441-1244")
                .document("112-33-2321")
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .address(buildAddressEmbeddable())
                .build();
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

    private static RecipientEmbeddable buildRecipientEmbeddable(){
        return RecipientEmbeddable.builder()
                .phone("111-441-1244")
                .firstName("John")
                .lastName("Doe")
                .document("112-33-2321")
                .build();
    }

}