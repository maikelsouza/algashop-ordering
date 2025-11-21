package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.AbstractDomainIT;
import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.order.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;
import java.util.List;
import java.util.Optional;


@Import({OrdersPersistenceProvider.class, OrderPersistenceEntityAssembler.class,
        OrderPersistenceEntityDisassembler.class, CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class, CustomerPersistenceEntityDisassembler.class})
class OrdersIT extends AbstractDomainIT {

    private Orders orders;

    private Customers customers;


    @Autowired
    public OrdersIT(Orders orders, Customers customers) {
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    public void setup(){
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)){
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
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

    @Test
    public void shouldUpdateExistingOrder(){
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();
        order.markAsPaid();

        orders.add(order);
        order = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(order.isPaid()).isTrue();
    }

    @Test
    public void shouldNotAllowStalesUpdates(){
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        orders.add(order);
        Order orderT1 = orders.ofId(order.id()).orElseThrow();
        Order orderT2 = orders.ofId(order.id()).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);
        orderT2.cancel();

        Assertions.assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() ->orders.add(orderT2));

        Order savedOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(savedOrder.canceledAt()).isNull();
        Assertions.assertThat(savedOrder.paidAt()).isNotNull();
    }

    @Test
    public void shouldCountExistingOrders(){

        Assertions.assertThat(orders.count()).isZero();

        Order order1 = OrderTestDataBuilder.anOrder().build();
        Order order2 = OrderTestDataBuilder.anOrder().build();

        orders.add(order1);
        orders.add(order2);

        Assertions.assertThat(orders.count()).isEqualTo(2);
    }

    @Test
    public void shouldReturnIfOrderExists(){

        Order order = OrderTestDataBuilder.anOrder().build();
        orders.add(order);

        Assertions.assertThat(orders.exists(order.id())).isTrue();
        Assertions.assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    public void shouldListExistingOrdersByYear(){
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Order order3 = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        Order order4 = OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        List<Order> orderList = orders.placedByCustomerInYear(customerId, Year.now());

        Assertions.assertThat(orderList).isNotEmpty();
        Assertions.assertThat(orderList.size()).isEqualTo(2);

        orderList = orders.placedByCustomerInYear(customerId, Year.now().minusYears(1));
        Assertions.assertThat(orderList).isEmpty();

        orderList = orders.placedByCustomerInYear(new CustomerId(), Year.now().minusYears(1));
        Assertions.assertThat(orderList).isEmpty();
  }

    @Test
    public void shouldReturnTotalSoldByCustomer() {
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order3 = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        Order order4 = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        Money expectedTotalAmount = order1.totalAmount().add(order2.totalAmount());

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Assertions.assertThat(orders.totalSoldForCustomer(customerId)).isEqualTo(expectedTotalAmount);
        Assertions.assertThat(orders.totalSoldForCustomer(new CustomerId())).isEqualTo(Money.ZERO);
    }

    @Test
    public void shouldReturnSalesQuantityByCustomer() {
        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order3 = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();
        Order order4 = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        orders.add(order1);
        orders.add(order2);
        orders.add(order3);
        orders.add(order4);

        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Assertions.assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now())).isEqualTo(2);
        Assertions.assertThat(orders.salesQuantityByCustomerInYear(customerId, Year.now().minusYears(1))).isZero();
    }

}