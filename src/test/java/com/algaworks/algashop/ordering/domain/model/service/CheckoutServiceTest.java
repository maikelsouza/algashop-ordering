package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.*;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT;

class CheckoutServiceTest {


    private final CheckoutService checkoutService = new CheckoutService();


    @Test
    void givenShoppingCartWithItems_shouldToDoCheckout(){
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.id()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("6210"));
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenShoppingCartNoItems_whenTryToDoCheckout_shouldGenerationException (){

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod))
                .withMessage(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT,shoppingCart.id()));
    }

    @Test
    void givenShoppingCartWithItemsUnAvailable_whenTryTodoCheckout_shouldGenerationException(){

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Product productUnavailable = ProductTestDataBuilder.aProduct().inStock(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.refreshItem(productUnavailable);

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod))
                .withMessage(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT,shoppingCart.id()));

    }

}