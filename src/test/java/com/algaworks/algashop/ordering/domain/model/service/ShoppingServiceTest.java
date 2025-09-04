package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHaveShoppingCartException;

import java.util.Optional;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {

    @Mock
    private ShoppingCarts shoppingCarts;

    @Mock
    private Customers customers;

    @InjectMocks
    private ShoppingService shoppingService;

    @Test
    public void givenCustomerId_whenStartShopping_shouldReturnShoppingCart(){

        when(customers.exists(Mockito.any(CustomerId.class)))
                .thenReturn(true);

        when(shoppingCarts.ofCustomer(Mockito.any(CustomerId.class)))
                .thenReturn(Optional.empty());

        ShoppingCart shoppingCart = shoppingService.startShopping(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);

        assertThat(shoppingCart).isNotNull();
        assertThat(shoppingCart.customerId()).isEqualTo(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);
        assertThat(shoppingCart.isEmpty()).isTrue();
        assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);

        verify(customers).exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);
        verify(shoppingCarts).ofCustomer(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID);
    }


    @Test
    void givenNotExistingCustomer_whenStartShopping_shouldGenerationException() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        when(customers.exists(customerId)).thenReturn(false);

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId))
                .withMessage(String.format(ERROR_CUSTOMER_NOT_FOUND,customerId));

        verify(customers).exists(customerId);
        verify(shoppingCarts, never()).ofCustomer(any());
    }

    @Test
    void givenCustomerWhitShoppingCart_whenStartShopping_shouldGenerationException() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        ShoppingCart existing = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();

        when(customers.exists(customerId)).thenReturn(true);
        when(shoppingCarts.ofCustomer(customerId)).thenReturn(Optional.of(existing));

        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> shoppingService.startShopping(customerId))
                .withMessage(String.format(ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART,customerId));

        verify(customers).exists(customerId);
        verify(shoppingCarts).ofCustomer(customerId);
    }


}