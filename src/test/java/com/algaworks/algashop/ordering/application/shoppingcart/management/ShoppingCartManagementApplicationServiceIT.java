package com.algaworks.algashop.ordering.application.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.product.*;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class ShoppingCartManagementApplicationServiceIT {

    @Autowired
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;


    @Test
    public void givenAShoppingCartItemInput_whenAddItem_shouldSuccessfully(){

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        Product product = ProductTestDataBuilder.aProduct().build();

        Mockito.when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(product.id().value())
                .quantity(1)
                .build();

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);

        ShoppingCart updatedCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(updatedCart.isEmpty()).isFalse();
        Assertions.assertThat(updatedCart.totalItems()).isEqualTo(new Quantity(1));
        Assertions.assertThat(updatedCart.findItem(product.id()).productId()).isEqualTo(product.id());
        Assertions.assertThat(updatedCart.findItem(product.id()).quantity()).isEqualTo(new Quantity(1));

        verify(productCatalogService).ofId(product.id());
    }

    @Test
    void givenAShoppingCartItemInput_whenTryAddItemWithShoppingCartNotFoundE_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        Product product = ProductTestDataBuilder.aProduct().build();

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(product.id().value())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .withMessage(String.format(ERROR_SHOPPING_CARD_FOUND, shoppingCart.id()));
    }


    @Test
    void givenAShoppingCartItemInput_whenTryAddItemWithProductNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        Product product = ProductTestDataBuilder.aProduct().build();

        Mockito.when(productCatalogService.ofId(product.id())).thenReturn(Optional.empty());

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(product.id().value())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .withMessage(String.format(ERROR_PRODUCT_NOT_FOUND, product.id()));

        verify(productCatalogService).ofId(product.id());
    }

    @Test
    void givenAShoppingCartItemInput_whenTryAddItemWithProductIsOutStock_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCarts.add(shoppingCart);

        Product product = ProductTestDataBuilder.aProduct().inStock(false).build();

        Mockito.when(productCatalogService.ofId(product.id())).thenReturn(Optional.of(product));

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .productId(product.id().value())
                .quantity(1)
                .build();

        assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.addItem(shoppingCartItemInput))
                .withMessage(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK, product.id()));

        verify(productCatalogService).ofId(product.id());
    }


}