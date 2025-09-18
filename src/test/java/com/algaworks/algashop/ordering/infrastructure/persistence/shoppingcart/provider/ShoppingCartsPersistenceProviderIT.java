package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.provider;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.*;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@Import({ShoppingCartsPersistenceProvider.class, ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class, SpringDataAuditingConfig.class,
        CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class})
class ShoppingCartsPersistenceProviderIT {

    private ShoppingCartsPersistenceProvider persistenceProvider;

    private ShoppingCartPersistenceEntityRepository entityRepository;

    private CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public ShoppingCartsPersistenceProviderIT(ShoppingCartsPersistenceProvider persistenceProvider,
                                              ShoppingCartPersistenceEntityRepository entityRepository,
                                              CustomersPersistenceProvider customersPersistenceProvider) {
        this.persistenceProvider = persistenceProvider;
        this.entityRepository = entityRepository;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @BeforeEach
    public void setup(){
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)){
            customersPersistenceProvider.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @AfterEach
    void cleanUp() {
        entityRepository.deleteAll();
    }

    @Test
    public void shouldUpdateAndKeepPersistenceEntityState(){

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        UUID shoppingCartId = shoppingCart.id().value();

        persistenceProvider.add(shoppingCart);
        ShoppingCartPersistenceEntity persistenceEntity = entityRepository.findById(shoppingCartId).orElseThrow();

        Assertions.assertThat(persistenceEntity.getTotalItems()).isEqualTo(3);
        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

        shoppingCart = persistenceProvider.ofId(shoppingCart.id()).orElseThrow();
        shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(1));
        persistenceProvider.add(shoppingCart);

        persistenceEntity = entityRepository.findById(shoppingCartId).orElseThrow();
        Assertions.assertThat(persistenceEntity.getTotalItems()).isEqualTo(4);
        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }

    @Test
    void shouldFindShoppingCartByCustomerId() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        persistenceProvider.add(shoppingCart);

        Optional<ShoppingCart> shoppingCartOptional = persistenceProvider.ofCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

        Assertions.assertThat(shoppingCartOptional).isPresent();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldAddFindNotFailWhenNotTransaction(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        persistenceProvider.add(shoppingCart);

        Assertions.assertThatNoException()
                .isThrownBy(() -> persistenceProvider.ofId(shoppingCart.id()).orElseThrow());

    }




}