package com.algaworks.algashop.ordering.core.application.customer.loyaltypoints;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.*;
import com.algaworks.algashop.ordering.core.domain.model.order.*;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CustomerLoyaltyPointsApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @MockitoBean
    private CustomerEventListener eventListener;

    @Test
    public void givenACustomerAndAOrder_shouldAddLoyaltyPoints(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().value().toString());
        Customer customerUpdate = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(customerUpdate).isNotNull();
        Assertions.assertThat(customerUpdate.loyaltyPoints()).isNotNull();
        Assertions.assertThat(customerUpdate.loyaltyPoints()).isEqualTo(new LoyaltyPoints(15));
    }

    @Test
    void givenACustomerAndAOrder_whenTryAddLoyaltyPointsWithCustomerNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);

        UUID newCustomerId = new CustomerId().value();
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints( newCustomerId, order.id().value().toString()))
                .withMessage(String.format(ERROR_CUSTOMER_NOT_FOUND, newCustomerId));
    }

    @Test
    void givenACustomerAndAOrder_whenTryAddLoyaltyPointsWithOrderNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);

        String newOrderId = new OrderId().toString();
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints( customer.id().value(), newOrderId))
                .withMessage(String.format(ERROR_ORDER_NOT_FOUND, newOrderId));
    }

    @Test
    void givenACustomerAndAOrder_whenTryAddLoyaltyPointsWithClientThatHasAlreadyBeenArchived_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().archived(true).build();

        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(customer.id())
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);

        assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().value().toString()))
                .withMessage(ERROR_CUSTOMER_ARCHIVED);

    }

    @Test
    void givenOrderOfOneCustomer_whenTryAddLoyaltyPointsWithAnotherCustomer_shouldGenerationException() {

        Customer customerA = CustomerTestDataBuilder.brandNewCustomer().build();
        Customer customerB = CustomerTestDataBuilder.brandNewCustomer().build();

        customers.add(customerA);
        customers.add(customerB);
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(customerB.id())
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);


        assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints( customerA.id().value(), order.id().value().toString()))
                .withMessage(String.format(ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER,  order.id().value(), customerA.id().value()));
    }

    @Test
    void givenOrderNotReady_whenTryAddLoyaltyPoints_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.place();
        orders.add(order);

        assertThatExceptionOfType(CantAddLoyaltyPointsOrderIsNotReadyException.class)
                .isThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints( customer.id().value(), order.id().value().toString()))
                .withMessage(String.format(ERROR_CANT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY, order.id().value()));
    }

    @Test
    public void givenOrderBelowMinimumAmount_whenTryAddLoyaltyPoints_shouldPointsToZero(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        customers.add(customer);
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .withItems(false)
                .build();

        Product product = ProductTestDataBuilder.aProductAltMousePaD().build();

        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().value().toString());
        Customer customerUpdate = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(customerUpdate).isNotNull();
        Assertions.assertThat(customerUpdate.loyaltyPoints()).isNotNull();
        Assertions.assertThat(customerUpdate.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }
}