package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.provider;

import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@Import({OrdersPersistenceProvider.class, OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class, SpringDataAuditingConfig.class,
        CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration,classpath:db/testdata")
class OrdersPersistenceProviderIT {

    private OrdersPersistenceProvider persistenceProvider;

    private OrderPersistenceEntityRepository entityRepository;



    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider persistenceProvider, OrderPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.entityRepository = entityRepository;
    }

    @Test
    public void shouldUpdateAndKeepPersistenceEntityState(){

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Long orderId = order.id().value().toLong();

        persistenceProvider.add(order);
        OrderPersistenceEntity persistenceEntity = entityRepository.findById(orderId).orElseThrow();

        Assertions.assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PLACED.name());
        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();

        order = persistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        persistenceProvider.add(order);

        persistenceEntity = entityRepository.findById(orderId).orElseThrow();
        Assertions.assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PAID.name());
        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldAddFindNotFailWhenNotTransaction(){
        Order order = OrderTestDataBuilder.anOrder().build();
        persistenceProvider.add(order);

        Assertions.assertThatNoException()
                .isThrownBy(() -> persistenceProvider.ofId(order.id()).orElseThrow());

    }
}