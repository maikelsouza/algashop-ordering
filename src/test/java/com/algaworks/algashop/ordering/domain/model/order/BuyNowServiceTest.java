package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {


    private BuyNowService buyNowService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup(){
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                100,
                2,
                2000
        );
        buyNowService = new BuyNowService(specification);
    }


    @Test
    public void givenProduct_whenBuyNow_shouldBuy(){
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;
        Quantity quantity = new Quantity(1);


        Order order =  buyNowService.buyNow(product,customer, billing,
                shipping, quantity,creditCard);

        assertThat(order).satisfies(
                o -> assertThat(o).isNotNull(),
                o -> assertThat(o.isPlaced()).isTrue(),
                o -> assertThat(o.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD),
                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.totalItems()).isEqualTo(quantity),
                o -> assertThat(o.customerId()).isEqualTo(customer.id())
        );

        assertThat(order.items()).hasSize(1);
        assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(order.items().iterator().next().quantity()).isEqualTo(quantity);
        assertThat(order.items().iterator().next().price()).isEqualTo(product.price());

        Money expectedTotalAmount = product.price().multiply(quantity).add(shipping.cost());
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.totalItems()).isEqualTo(quantity);

    }

    @Test
    void givenProductWithItemUnAvailable_whenTryByNow_shouldGenerationException(){

        Product productUnavailable = ProductTestDataBuilder.aProduct().inStock(false).build();
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Quantity quantity = new Quantity(1);

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(ProductOutOfStockException.class)
                .isThrownBy(() ->buyNowService.buyNow(productUnavailable, customer,billing, shipping, quantity,creditCard))
                .withMessage(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK,productUnavailable.id()));
    }

    @Test
    void givenInvalidItemQuantity_whenTryBuyNow_shouldGenerationException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatThrownBy(() -> buyNowService.buyNow(product, customer, billing, shipping,
                Quantity.ZERO, paymentMethod)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenCustomerWithFreeShipping_whenBuyNow_shouldReturnPlacedOrderWithFreeShipping(){

        Mockito.when(orders.salesQuantityByCustomerInYear(
                Mockito.any(CustomerId.class),
                Mockito.any(Year.class))).thenReturn(2L);

        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(100)).build();
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;
        Quantity quantity = new Quantity(1);


        Order order =  buyNowService.buyNow(product,customer, billing,
                shipping, quantity,creditCard);

        assertThat(order).satisfies(
                o -> assertThat(o).isNotNull(),
                o -> assertThat(o.isPlaced()).isTrue(),
                o -> assertThat(o.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD),
                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build()),
                o -> assertThat(o.totalItems()).isEqualTo(quantity),
                o -> assertThat(o.customerId()).isEqualTo(customer.id())
        );

        assertThat(order.items()).hasSize(1);
        assertThat(order.items().iterator().next().productId()).isEqualTo(product.id());
        assertThat(order.items().iterator().next().quantity()).isEqualTo(quantity);
        assertThat(order.items().iterator().next().price()).isEqualTo(product.price());

        Money expectedTotalAmount = product.price().multiply(quantity);
        assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.totalItems()).isEqualTo(quantity);
    }

}