package com.algaworks.algashop.ordering.core.application.shoppingcart;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.*;
import com.algaworks.algashop.ordering.core.domain.model.product.*;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.*;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.listener.shoppingcart.ShoppingCartEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.UUID;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

class ShoppingCartManagementApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoSpyBean
    private ShoppingCartEventListener shoppingCartEventListener;



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
        verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartItemAddedEvent.class));

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
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND, shoppingCart.id()));
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

    @Test
    public void givenACustomer_whenCreateNew_shouldSuccessfully(){

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        customers.add(customer);

        UUID newShoppingCartId  = shoppingCartManagementApplicationService.createNew(customer.id().value());

        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(newShoppingCartId)).orElseThrow();
        Assertions.assertThat(newShoppingCartId).isNotNull();
        Assertions.assertThat(shoppingCart.customerId()).isEqualTo(customer.id());
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartCreatedEvent.class));
    }

    @Test
    void givenACustomer_whenTryCreateNewWithoutCustomer_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        UUID customerId = customer.id().value();

        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.createNew(customerId))
                .withMessage(String.format(ERROR_CUSTOMER_NOT_FOUND,customerId));
    }

    @Test
    void givenACustomer_whenTryCreateNewWithCustomerAlreadyHavesShoppingCart_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        UUID customerId = customer.id().value();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(new CustomerId(customerId)).build();
        customers.add(customer);
        shoppingCarts.add(shoppingCart);

        assertThatExceptionOfType(CustomerAlreadyHaveShoppingCartException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.createNew(customerId))
                .withMessage(String.format(ERROR_CUSTOMER_ALREADY_HAVES_SHOPPING_CART,customerId));
    }

    @Test
    public void givenAShoppingCartAndAShoppingCartItem_whenRemoveItem_shouldSuccessfully(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);
        ShoppingCartItem itemToRemove = shoppingCart.items().iterator().next();

        shoppingCartManagementApplicationService.removeItem(shoppingCart.id().value(), itemToRemove.id().value());

        shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCart.id().value())).orElseThrow();

        Assertions.assertThat(shoppingCart).isNotNull();
        Assertions.assertThat(shoppingCart.customerId()).isEqualTo(customerId);
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartItemRemovedEvent.class));
    }

    @Test
    void givenAShoppingCartAndAShoppingCartItem_whenTryRemoveItemWithShoppingCartNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        ShoppingCartItem itemToRemove = shoppingCart.items().iterator().next();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.removeItem(shoppingCart.id().value(), itemToRemove.id().value()))
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND,shoppingCart.id()));

    }

    @Test
    void givenAShoppingCartAndAShoppingCartItem_whenTryRemoveItemWithShoppingCartDoesNotContain_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        shoppingCarts.add(shoppingCart);

        UUID nonExistingItemId = UUID.randomUUID();

        assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.removeItem(shoppingCart.id().value(), nonExistingItemId))
                .withMessage(String.format(ERROR_SHOPPING_CARD_DOES_NOT_CONTAIN_ITEM,shoppingCart.id(),nonExistingItemId));
    }

    @Test
    void givenAShoppingCart_whetEmpty_shouldSuccessfully(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);

        shoppingCartManagementApplicationService.empty(shoppingCart.id().value());

        shoppingCart = shoppingCarts.ofId(new ShoppingCartId(shoppingCart.id().value())).orElseThrow();

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
        verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartEmptiedEvent.class));
    }

    @Test
    void givenAShoppingCartAndAShoppingCartItem_whenTryEmptyWithShoppingCartNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.empty(shoppingCart.id().value()))
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND,shoppingCart.id()));
    }

    @Test
    void givenAShoppingCart_whetDelete_shouldSuccessfully(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);

        shoppingCartManagementApplicationService.delete(shoppingCart.id().value());

        Optional<ShoppingCart> shoppingCartOptional = shoppingCarts.ofId(new ShoppingCartId(shoppingCart.id().value()));

        Assertions.assertThat(shoppingCartOptional).isNotPresent();
    }

    @Test
    void givenAShoppingCartAndAShoppingCartItem_whenTryDeleteWithShoppingCartNotFound_shouldGenerationException() {

        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerId customerId = customer.id();
        customers.add(customer);
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customerId).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(product, new Quantity(1));

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> shoppingCartManagementApplicationService.delete(shoppingCart.id().value()))
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND,shoppingCart.id()));
    }






}