package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.*;
import com.algaworks.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.exception.ErrorMessages.ERROR_PRODUCT_IS_OUT_OF_STOCK;
import static org.assertj.core.api.Assertions.assertThat;

class BuyNowServiceTest {

    private final BuyNowService buyNowService = new BuyNowService();


    @Test
    public void givenProduct_whenBuyNow_shouldBuy(){
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;
        Quantity quantity = new Quantity(1);


        Order order =  buyNowService.buyNow(product,customer.id(), billing,
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
                .isThrownBy(() ->buyNowService.buyNow(productUnavailable, customer.id(),billing, shipping, quantity,creditCard))
                .withMessage(String.format(ERROR_PRODUCT_IS_OUT_OF_STOCK,productUnavailable.id()));
    }

    @Test
    void givenInvalidItemQuantity_whenTryBuyNow_shouldGenerationException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatThrownBy(() -> buyNowService.buyNow(product, customer.id(), billing, shipping,
                Quantity.ZERO, paymentMethod)).isInstanceOf(IllegalArgumentException.class);
    }

}