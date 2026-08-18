package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.AbstractPersistenceIT;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

@Import(SpringDataAuditingConfig.class)
class ShoppingCartPersistenceEntityRepositoryIT extends AbstractPersistenceIT {

    private final ShoppingCartPersistenceEntityRepository shoppingCartPersistenceEntityRepository;

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @Autowired
    public ShoppingCartPersistenceEntityRepositoryIT(ShoppingCartPersistenceEntityRepository shoppingCartPersistenceEntityRepository, CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.shoppingCartPersistenceEntityRepository = shoppingCartPersistenceEntityRepository;
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    @BeforeEach
    public void setup(){
        shoppingCartPersistenceEntityRepository.deleteAll();
        shoppingCartPersistenceEntityRepository.flush();

        UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
        customerPersistenceEntity = customerPersistenceEntityRepository.findById(customerId)
                .orElseGet(() -> customerPersistenceEntityRepository
                        .saveAndFlush(CustomerPersistenceEntityTestDataBuilder.existingCustomer().build()));

        Assertions.assertThat(customerPersistenceEntity).isNotNull();
        Assertions.assertThat(customerPersistenceEntity.getId()).isEqualTo(customerId);
    }

    @Test
    public void shouldPersist(){
        ShoppingCartPersistenceEntity entity = ShoppingCartPersistenceEntityTestDataBuilder
                .existingShoppingCart()
                .customer(customerPersistenceEntity)
                .build();

        Assertions.assertThat(entity.getCustomerId()).isNotNull();

        shoppingCartPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(shoppingCartPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        ShoppingCartPersistenceEntity savedEntity = shoppingCartPersistenceEntityRepository.findById(entity.getId()).orElseThrow();

        Assertions.assertThat(savedEntity.getItems()).isNotEmpty();
    }

    @Test
    public void shouldCount(){
        long count = shoppingCartPersistenceEntityRepository.count();

        Assertions.assertThat(count).isZero();
    }


    @Test
    public void shouldSetAuditingValues(){
        ShoppingCartPersistenceEntity persistenceEntity = ShoppingCartPersistenceEntityTestDataBuilder
                .existingShoppingCart()
                .customer(customerPersistenceEntity)
                .build();

        Assertions.assertThat(persistenceEntity.getCustomerId()).isNotNull();

        persistenceEntity = shoppingCartPersistenceEntityRepository.saveAndFlush(persistenceEntity);

        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }
}