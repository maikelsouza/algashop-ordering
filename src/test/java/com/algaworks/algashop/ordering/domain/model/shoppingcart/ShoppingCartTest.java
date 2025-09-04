package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.HashSet;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.*;

class ShoppingCartTest {

    @Test
    public void givenShoppingCart_whenEmptyShoppingCartItem_shouldZeroMoneyAndZeroQuantity(){

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertWith(shoppingCart,
                s -> Assertions.assertThat(s.totalAmount()).isEqualTo(Money.ZERO),
                s -> Assertions.assertThat(s.totalItems()).isEqualTo(Quantity.ZERO),
                s -> Assertions.assertThat(s.isEmpty()).isTrue());
    }

    @Test
    public void givenShoppingCart_whenShoppingCartItemOutStock_shouldGenerateException(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product productUnavailable = ProductTestDataBuilder.aProductUnavailable().build();

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() -> shoppingCart.addItem(productUnavailable, new Quantity(1)))
                .withMessage(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK,productUnavailable.id()));

    }

    @Test
    public void givenShoppingCart_whenAddSameItemTwice_shouldSumQuantity(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product product = ProductTestDataBuilder.aProduct().build();

        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.addItem(product, new Quantity(2));

        Assertions.assertWith(shoppingCart,
                s -> Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(new Money("9000")),
                s -> Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3)));
    }

    @Test
    public void givenTwoShoppingCarts_whenTwoDifferentProducts_shouldSumPrince(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Product productMousePad = ProductTestDataBuilder.aProductAltMousePaD().build();

        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.addItem(productMousePad, new Quantity(1));

        Assertions.assertWith(shoppingCart,
                s -> Assertions.assertThat(s.totalItems()).isEqualTo(new Quantity(2)),
                s-> Assertions.assertThat(s.totalAmount()).isEqualTo(new Money("3100")));

    }

    @Test
    public void givenShoppingCart_whenRemoveItemNotExist_shouldGenerationException(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainItemException.class)
                .isThrownBy(() ->shoppingCart.removeItem(shoppingCartItemId))
                .withMessage(String.format(ERROR_SHOPPING_CARD_DOES_NOT_CONTAIN_ITEM,shoppingCart.id(), shoppingCartItemId));
    }

    @Test
    public void givenShoppingCart_whenEmptyShoppingCart_shouldZeroTotals(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCart.empty();

        Assertions.assertWith(shoppingCart,
                s-> Assertions.assertThat(s.totalAmount()).isEqualTo(Money.ZERO),
                s-> Assertions.assertThat(s.totalItems()).isEqualTo(Quantity.ZERO),
                s-> Assertions.assertThat(s.isEmpty()).isTrue());
    }

    @Test
    public void givenShoppingCart_whenUpdateIncompatibleProduct_shouldGenerationException(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Product productMousePad = ProductTestDataBuilder.aProductAltMousePaD().build();

        shoppingCart.addItem(product, new Quantity(1));

        Assertions.assertThatExceptionOfType(ShoppingCartDoesNotContainProductException.class)
                .isThrownBy(() -> shoppingCart.refreshItem(productMousePad))
                .withMessage(String.format(ERROR_SHOPPING_CARD_DOES_NOT_CONTAIN_PRODUCT,shoppingCart.id(), productMousePad.id()));
    }

    @Test
    public void givenTwoShoppingCarts_wheSameIds_shouldBeEquals(){

        ShoppingCartId shoppingCartId = new ShoppingCartId();

        ShoppingCart shoppingCart = ShoppingCart.existing()
                .id(shoppingCartId)
                .customerId(new CustomerId())
                .totalAmount(new Money("10"))
                .totalItems(new Quantity(1))
                .createdAt(OffsetDateTime.now())
                .items(new HashSet<>())
                .build();

        ShoppingCart shoppingCart2 = ShoppingCart.existing()
                .id(shoppingCartId)
                .customerId(new CustomerId())
                .totalAmount(new Money("10"))
                .totalItems(new Quantity(1))
                .createdAt(OffsetDateTime.now())
                .items(new HashSet<>())
                .build();


        Assertions.assertThat(shoppingCart).isEqualTo(shoppingCart2);
    }




}