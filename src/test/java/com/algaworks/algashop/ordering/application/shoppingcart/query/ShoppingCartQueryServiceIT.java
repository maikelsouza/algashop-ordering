package com.algaworks.algashop.ordering.application.shoppingcart.query;

import com.algaworks.algashop.ordering.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_NOT_FOUND;
import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_NOT_FOUND_FOR_CUSTOMER;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class ShoppingCartQueryServiceIT extends AbstractApplicationIT {

    @Autowired
    private ShoppingCartQueryService queryService;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;


    @Test
    public void shouldFindById(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput output = queryService.findById(shoppingCart.id().value());

        Assertions.assertThat(output.getId()).isEqualTo(shoppingCart.id().value());
        Assertions.assertThat(output.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
        Assertions.assertThat(output.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
        Assertions.assertThat(output.getCustomerId()).isEqualTo(shoppingCart.customerId().value());
        Assertions.assertThat(output.getItems().size()).isEqualTo(shoppingCart.items().size());
    }


    @Test
    void givenAShoppingCartId_whenTryFindByIdWithShoppingCartNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        UUID uuid = new CustomerId().value();
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> queryService.findById(uuid))
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND, uuid));
    }

    @Test
    public void shouldFindByCustomerId(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartOutput output = queryService.findByCustomerId(customer.id().value());

        Assertions.assertThat(output.getId()).isEqualTo(shoppingCart.id().value());
        Assertions.assertThat(output.getTotalAmount()).isEqualTo(shoppingCart.totalAmount().value());
        Assertions.assertThat(output.getTotalItems()).isEqualTo(shoppingCart.totalItems().value());
        Assertions.assertThat(output.getCustomerId()).isEqualTo(shoppingCart.customerId().value());
        Assertions.assertThat(output.getItems().size()).isEqualTo(shoppingCart.items().size());
    }

    @Test
    void givenACustomerId_whenTryFindByIdWithShoppingCartNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.existingCustomer().id(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);

        UUID uuid = new CustomerId().value();
        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> queryService.findByCustomerId(uuid))
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND_FOR_CUSTOMER, uuid));
    }


}