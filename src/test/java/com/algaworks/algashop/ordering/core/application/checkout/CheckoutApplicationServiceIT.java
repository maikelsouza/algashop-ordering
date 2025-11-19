package com.algaworks.algashop.ordering.core.application.checkout;

import com.algaworks.algashop.ordering.core.application.AbstractApplicationIT;
import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.*;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT;
import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;

class CheckoutApplicationServiceIT extends AbstractApplicationIT {

    @Autowired
    private CheckoutApplicationService checkoutApplicationService;

    @Autowired
    private ShoppingCarts shoppingCarts;

    @Autowired
    private Customers customers;

    @Autowired
    private Orders orders;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @BeforeEach
    public void setup() {
        Mockito.when(shippingCostService.calculate(Mockito.any(ShippingCostService.CalculationRequest.class)))
                .thenReturn(new ShippingCostService.CalculationResult(
                        new Money("10.00"),
                        LocalDate.now().plusDays(3)
                ));

        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }


    @Test
    public void givenACheckoutInput_whenCheckout_shouldSuccessfully(){

        Product product = ProductTestDataBuilder.aProduct().build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCarts.add(shoppingCart);
        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        String orderIdString = checkoutApplicationService.checkout(checkoutInput);
        OrderId orderId = new OrderId(orderIdString);

        shoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(orderIdString).isNotNull();
        Assertions.assertThat(orderIdString).isNotBlank();
        Assertions.assertThat(shoppingCart).isNotNull();
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(orders.exists(orderId)).isTrue();

        Order order = orders.ofId(orderId).orElseThrow();

        Assertions.assertThat(order).satisfies(
                o -> Assertions.assertThat(order.id()).isEqualTo(orderId),
                o -> Assertions.assertThat(order.customerId()).isEqualTo(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID),
                o -> Assertions.assertThat(order.isPlaced()).isTrue(),
                o -> Assertions.assertThat(order.placedAt()).isNotNull()
        );
        Mockito.verify(orderEventListener).listen(Mockito.any(OrderPlacedEvent.class));
        verify(shippingCostService).calculate(Mockito.any(ShippingCostService.CalculationRequest.class));
    }

    @Test
    void givenACheckoutInput_whenTryCheckoutWithShoppingCartNotFound_shouldGenerationException() {

        Product product = ProductTestDataBuilder.aProduct().build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        assertThatExceptionOfType(ShoppingCartNotFoundException.class)
                .isThrownBy(() -> checkoutApplicationService.checkout(checkoutInput))
                .withMessage(String.format(ERROR_SHOPPING_CARD_NOT_FOUND, shoppingCart.id()));
    }

    @Test
    void givenACheckoutInput_whenTryCheckoutWithShoppingCartCantProceedToCheckout_shouldGenerationException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);
        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput()
                .shoppingCartId(shoppingCart.id().value())
                .build();

        assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutApplicationService.checkout(checkoutInput))
                .withMessage(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT, shoppingCart.id()));

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        verify(shippingCostService).calculate(Mockito.any(ShippingCostService.CalculationRequest.class));
    }

}