package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ShoppingCartPersistenceEntityAssemblerTest {

    @Mock
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @InjectMocks
    private ShoppingCartPersistenceEntityAssembler assembler;

    @BeforeEach
    public void setup(){
        Mockito.when(customerPersistenceEntityRepository.getReferenceById(Mockito.any(UUID.class)))
                .then(a -> {
                    UUID customerId = a.getArgument(0, UUID.class);
                    return CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(customerId).build();
                });
    }

    @Test
    void shouldConvertToDomain(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();;
        ShoppingCartPersistenceEntity shoppingCartPersistenceEntity = assembler.fromDomain(shoppingCart);

        Assertions.assertThat(shoppingCartPersistenceEntity).satisfies(
                c-> Assertions.assertThat(c.getId()).isEqualTo(shoppingCart.id().value()),
                c-> Assertions.assertThat(c.getCustomerId()).isEqualTo(shoppingCart.customerId().value()),
                c-> Assertions.assertThat(c.getTotalItems()).isEqualTo(shoppingCart.totalItems().value()),
                c-> Assertions.assertThat(c.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value()),
                c-> Assertions.assertThat(c.getCreatedAt()).isEqualTo(shoppingCart.createdAt())
        );
    }

}