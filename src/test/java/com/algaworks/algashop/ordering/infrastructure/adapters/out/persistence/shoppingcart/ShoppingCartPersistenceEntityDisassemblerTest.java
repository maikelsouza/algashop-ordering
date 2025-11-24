package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ShoppingCartPersistenceEntityDisassemblerTest {

    private final ShoppingCartPersistenceEntityDisassembler disassembler = new ShoppingCartPersistenceEntityDisassembler();

    @Test
    public void shouldConvertFromPersistence(){

        ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart().build();
        ShoppingCart domainEntity = disassembler.toDomainEntity(persistenceEntity);
        Assertions.assertThat(domainEntity).satisfies(
                s-> Assertions.assertThat(s.customerId()).isEqualTo(new CustomerId(persistenceEntity.getCustomerId())),
                s-> Assertions.assertThat(s.totalAmount()).isEqualTo(new Money(persistenceEntity.getTotalAmount())),
                s-> Assertions.assertThat(s.totalItems()).isEqualTo(new Quantity(persistenceEntity.getTotalItems())),
                s-> Assertions.assertThat(s.createdAt()).isEqualTo(persistenceEntity.getCreatedAt())
        );
    }

}