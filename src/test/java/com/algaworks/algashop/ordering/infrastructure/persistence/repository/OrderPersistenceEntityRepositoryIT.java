package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.AbstractPersistenceIT;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;


@Import(SpringDataAuditingConfig.class)
class OrderPersistenceEntityRepositoryIT extends AbstractPersistenceIT {


    private final OrderPersistenceEntityRepository orderPersistenceEntityRepository;

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private CustomerPersistenceEntity customerPersistenceEntity;

    @Autowired
    public OrderPersistenceEntityRepositoryIT(OrderPersistenceEntityRepository orderPersistenceEntityRepository, CustomerPersistenceEntityRepository customersPersistenceProvider, CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.orderPersistenceEntityRepository = orderPersistenceEntityRepository;
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;

    }

    @BeforeEach
    public void setup(){
        UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();
        if (!customerPersistenceEntityRepository.existsById(customerId)){
            customerPersistenceEntity = customerPersistenceEntityRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    public void shouldPersist(){
        OrderPersistenceEntity entity = OrderPersistenceEntityTestDataBuilder
                .existingOrder()
                .customer(customerPersistenceEntity)
                .build();
        orderPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(orderPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        OrderPersistenceEntity savedEntity = orderPersistenceEntityRepository.findById(entity.getId()).orElseThrow();

        Assertions.assertThat(savedEntity.getItems()).isNotEmpty();
    }

    @Test
    public void shouldCount(){
        long count = orderPersistenceEntityRepository.count();

        Assertions.assertThat(count).isZero();
    }

    @Test
    public void shouldSetAuditingValues(){
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder
                .existingOrder()
                .customer(customerPersistenceEntity)
                .build();
        persistenceEntity = orderPersistenceEntityRepository.saveAndFlush(persistenceEntity);

        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }

}