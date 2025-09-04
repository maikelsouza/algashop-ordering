package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;


import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.*;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class OrderPersistenceEntityAssemblerTest {


    @Mock
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @InjectMocks
    private OrderPersistenceEntityAssembler assembler;

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
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistenceEntity orderPersistenceEntity = assembler.fromDomain(order);

        Assertions.assertThat(orderPersistenceEntity).satisfies(
          p-> Assertions.assertThat(p.getId()).isEqualTo(order.id().value().toLong()),
          p-> Assertions.assertThat(p.getCustomerId()).isEqualTo(order.customerId().value()),
          p-> Assertions.assertThat(p.getTotalAmount()).isEqualTo(order.totalAmount().value()),
          p-> Assertions.assertThat(p.getTotalItems()).isEqualTo(order.totalItems().value()),
          p-> Assertions.assertThat(p.getStatus()).isEqualTo(order.status().name()),
          p-> Assertions.assertThat(p.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
          p-> Assertions.assertThat(p.getPlacedAt()).isEqualTo(order.placedAt()),
          p-> Assertions.assertThat(p.getPaidAt()).isEqualTo(order.paidAt()),
          p-> Assertions.assertThat(p.getCancelAt()).isEqualTo(order.canceledAt()),
          p-> Assertions.assertThat(p.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

    @Test
    void givenOrderWithNotItems_shouldRemovePersistenceEntityItems(){
        Order order = OrderTestDataBuilder.anOrder().withItems(false).build();
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();

        Assertions.assertThat(order.items()).isEmpty();
        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();

        assembler.merge(persistenceEntity,order);

        Assertions.assertThat(persistenceEntity.getItems()).isEmpty();
    }

    @Test
    void givenOrderWithItems_shouldAddToPersistenceEntity(){
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().items(new HashSet<>()).build();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(persistenceEntity.getItems()).isEmpty();

        assembler.merge(persistenceEntity,order);

        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(persistenceEntity.getItems().size()).isEqualTo(order.items().size());
    }

    @Test
    void givenOrderWithItems_whenMerge_shouldRemoveMergeCorrectly(){
        Order order = OrderTestDataBuilder.anOrder().withItems(true).build();

        Assertions.assertThat(order.items().size()).isEqualTo(2);

        Set<OrderItemPersistenceEntity> orderItemPersistenceEntities = order.items().stream().map(assembler::fromDomain).collect(Collectors.toSet());
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder
                .existingOrder()
                .items(orderItemPersistenceEntities)
                .build();

        OrderItemId orderItemId = order.items().iterator().next().id();
        order.removeItem(orderItemId);

        assembler.merge(persistenceEntity, order);

        Assertions.assertThat(persistenceEntity.getItems()).isNotEmpty();
        Assertions.assertThat(persistenceEntity.getItems().size()).isEqualTo(order.items().size());
    }
}