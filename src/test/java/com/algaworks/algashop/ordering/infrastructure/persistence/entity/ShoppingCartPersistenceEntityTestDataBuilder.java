package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class ShoppingCartPersistenceEntityTestDataBuilder {

    private ShoppingCartPersistenceEntityTestDataBuilder() {}


    public static ShoppingCartPersistenceEntity.ShoppingCartPersistenceEntityBuilder existingShoppingCart() {
        return ShoppingCartPersistenceEntity
                .builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .totalAmount(new BigDecimal("1250"))
                .totalItems(2)
                .customer(CustomerPersistenceEntityTestDataBuilder.existingCustomer().build())
                .createdAt(OffsetDateTime.now())
                .items(Set.of(existingItem().build(), existingItemAlt().build()));
    }

    private static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItem(){
        return ShoppingCartItemPersistenceEntity.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .price(new BigDecimal(500))
                .quantity(2)
                .totalAmount(new BigDecimal(1000))
                .available(true)
                .productName("Notebook")
                .productId(IdGenerator.generateTimeBasedUUID());
    }

    private static ShoppingCartItemPersistenceEntity.ShoppingCartItemPersistenceEntityBuilder existingItemAlt(){
        return ShoppingCartItemPersistenceEntity.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .price(new BigDecimal(250))
                .quantity(1)
                .available(true)
                .totalAmount(new BigDecimal(250))
                .productName("Mouse Pad")
                .productId(IdGenerator.generateTimeBasedUUID());
    }
}
