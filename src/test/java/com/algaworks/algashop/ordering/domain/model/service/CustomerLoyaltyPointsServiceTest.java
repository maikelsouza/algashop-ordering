package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.*;
import com.algaworks.algashop.ordering.domain.model.exception.CantAddLoyaltyPointsOrderIsNotReadyException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_CANT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY;
import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER;

class CustomerLoyaltyPointsServiceTest {

    CustomerLoyaltyPointsService customerLoyaltyPointsService = new CustomerLoyaltyPointsService();


    @Test
    public void givenValidCustomerAndOrder_whenAddingPoints_shouldAccumulate(){

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();

        customerLoyaltyPointsService.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    public void givenValidCustomerAndOrderWhitLowTotalAmount_whenAddingPoints_shouldNotAccumulate(){

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProductAltRamMemory().build();

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).build();
        order.addItem(product, new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();

        customerLoyaltyPointsService.addPoints(customer, order);

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(0));
    }

    @Test
    public void givenOrder_whenTryAddPoints_shouldGenerateException(){
        CustomerId customerId = new CustomerId();
        Customer customer = CustomerTestDataBuilder.existingCustomer(). build();
        Order order = OrderTestDataBuilder.anOrder().customerId(customerId).build();

        Assertions.assertThatExceptionOfType(OrderNotBelongsToCustomerException.class)
                .isThrownBy(() -> customerLoyaltyPointsService.addPoints(customer, order))
                .withMessage(String.format(ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER,order.id(), customer.id()));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"READY"})
    public void givenOrderNotReady_whenTryAddPoints_shouldGenerateException(OrderStatus orderStatus){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Order order = OrderTestDataBuilder.anOrder().status(orderStatus).build();

        Assertions.assertThatExceptionOfType(CantAddLoyaltyPointsOrderIsNotReadyException.class)
                .isThrownBy(() -> customerLoyaltyPointsService.addPoints(customer, order))
                .withMessage(String.format(ERROR_CANT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY,order.id()));
    }

}