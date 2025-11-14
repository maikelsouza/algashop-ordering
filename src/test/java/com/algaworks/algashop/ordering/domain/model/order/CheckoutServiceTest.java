package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {


    private CheckoutService checkoutService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup(){
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                new LoyaltyPoints(100),
                2L,
                new LoyaltyPoints(2000)
        );
        checkoutService = new CheckoutService(specification);
    }


    @Test
    void givenShoppingCartWithItems_shouldToDoCheckout(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());

        shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;


        Money shoppingCartTotalAmount = shoppingCart.totalAmount();
        Money expectedTotalAmountWithShipping = shoppingCartTotalAmount.add(shipping.cost());
        Quantity expectedOrderTotalItems = shoppingCart.totalItems();
        int expectedOrderItemsCount = shoppingCart.items().size();
        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, new CreditCardId());

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.id()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money("6210"));
        Assertions.assertThat(order.isPlaced()).isTrue();
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(order.totalAmount()).isEqualTo(expectedTotalAmountWithShipping);
        Assertions.assertThat(order.totalItems()).isEqualTo(expectedOrderTotalItems);
        Assertions.assertThat(order.items()).hasSize(expectedOrderItemsCount);

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);

    }

    @Test
    void givenShoppingCartNoItems_whenTryToDoCheckout_shouldGenerationException (){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, new CreditCardId()))
                .withMessage(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT,shoppingCart.id()));

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenShoppingCartWithItemsUnAvailable_whenTryTodoCheckout_shouldGenerationException(){
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Product productUnavailable = ProductTestDataBuilder.aProduct().inStock(false).build();
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.refreshItem(productUnavailable);

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() ->checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod, new CreditCardId()))
                .withMessage(String.format(ERROR_SHOPPING_CARD_CANT_PROCEED_TO_CHECKOUT,shoppingCart.id()));

        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();
        Assertions.assertThat(shoppingCart.items()).hasSize(1);

    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCheckout_shouldNotModifyShoppingCartState() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        Product productInStock = ProductTestDataBuilder.aProduct().build();
        shoppingCart.addItem(productInStock, new Quantity(2));

        Product productAlt = ProductTestDataBuilder.aProductAltRamMemory().build();
        shoppingCart.addItem(productAlt, new Quantity(1));

        Product productAltUnavailable = ProductTestDataBuilder.aProductAltRamMemory().id(productAlt.id()).inStock(false).build();
        shoppingCart.refreshItem(productAltUnavailable);

        Billing billingInfo = OrderTestDataBuilder.aBilling();
        Shipping shippingInfo = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(ShoppingCartCantProceedToCheckoutException.class)
                .isThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod, new CreditCardId()));

        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();

        Money expectedTotalAmount = productInStock.price()
                .multiply(new Quantity(2)).add(productAlt.price());
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(expectedTotalAmount);
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(new Quantity(3));
        Assertions.assertThat(shoppingCart.items()).hasSize(2);
    }

    @Test
    void givenValidShoppingCartAndCustomerWithFreeShipping_whenCheckout_shouldReturnPlacedOrderWithFreeShipping() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(3000)).build();

        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
        shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));


        Billing billingInfo = OrderTestDataBuilder.aBilling();
        Shipping shippingInfo = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Money shoppingCartTotalAmount = shoppingCart.totalAmount();
        Quantity expectedOrderTotalItems = shoppingCart.totalItems();
        int expectedOrderItemsCount = shoppingCart.items().size();

        Order order = checkoutService.checkout(customer, shoppingCart, billingInfo, shippingInfo, paymentMethod, new CreditCardId());

        Assertions.assertThat(order).isNotNull();
        Assertions.assertThat(order.id()).isNotNull();
        Assertions.assertThat(order.customerId()).isEqualTo(shoppingCart.customerId());
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billingInfo);
        Assertions.assertThat(order.shipping()).isEqualTo(shippingInfo.toBuilder().cost(Money.ZERO).build());
        Assertions.assertThat(order.isPlaced()).isTrue();

        Assertions.assertThat(order.totalAmount()).isEqualTo(shoppingCartTotalAmount);
        Assertions.assertThat(order.totalItems()).isEqualTo(expectedOrderTotalItems);
        Assertions.assertThat(order.items()).hasSize(expectedOrderItemsCount);

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
        Assertions.assertThat(shoppingCart.totalAmount()).isEqualTo(Money.ZERO);
        Assertions.assertThat(shoppingCart.totalItems()).isEqualTo(Quantity.ZERO);
    }

}