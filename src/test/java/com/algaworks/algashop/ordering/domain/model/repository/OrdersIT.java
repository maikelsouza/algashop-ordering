package com.algaworks.algashop.ordering.domain.model.repository;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@Import({OrdersPersistenceProvider.class, OrderPersistenceEntityAssembler.class})
class OrdersIT {

    private Orders orders;


    @Autowired
    public OrdersIT(Orders orders) {
        this.orders = orders;
    }

    @Test
    public void shouldPersistAndFind(){
        Order originalOrder = OrderTestDataBuilder.anOrder().build();
        OrderId orderId = originalOrder.id();

        orders.add(originalOrder);

        Optional<Order> optionalOrder = orders.ofId(orderId);

        Assertions.assertThat(optionalOrder.isPresent());

        Order savedOrder = optionalOrder.get();

        Assertions.assertThat(savedOrder).satisfies(
                s-> Assertions.assertThat(s.id()).isEqualTo(orderId),
                s-> Assertions.assertThat(s.customerId()).isEqualTo(originalOrder.customerId()),
                s-> Assertions.assertThat(s.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                s-> Assertions.assertThat(s.totalItems()).isEqualTo(originalOrder.totalItems()),
                s-> Assertions.assertThat(s.placedAt()).isEqualTo(originalOrder.placedAt()),
                s-> Assertions.assertThat(s.canceledAt()).isEqualTo(originalOrder.canceledAt()),
                s-> Assertions.assertThat(s.readyAt()).isEqualTo(originalOrder.readyAt()),
                s-> Assertions.assertThat(s.status()).isEqualTo(originalOrder.status()),
                s-> Assertions.assertThat(s.paymentMethod()).isEqualTo(originalOrder.paymentMethod())
        );
    }
}