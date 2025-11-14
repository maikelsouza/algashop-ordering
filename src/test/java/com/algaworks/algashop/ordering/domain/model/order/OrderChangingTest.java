package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_ORDER_CANNOT_BE_EDITED;

public class OrderChangingTest {

    @Test
    public void givenDraftOrder_whenChanging_shouldAllowChange(){

        Order order = OrderTestDataBuilder.anOrder().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;
        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(creditCard, new CreditCardId());


        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.isDraft()).isTrue(),
                o -> Assertions.assertThat(o.items()).isNotEmpty(),
                o -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.paymentMethod()).isEqualTo(creditCard)
                );
    }

    @Test
    public void givenNotDraftOrder_whenTryAddItem_shouldGenerateException(){
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();

        Product product = ProductTestDataBuilder.aProduct().build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.addItem(product, new Quantity(1)))
                .withMessage(String.format(ERROR_ORDER_CANNOT_BE_EDITED,order.id(), order.status()));
    }

    @Test
    public void givenNotDraftOrder_whenTryChangeBilling_shouldGenerateException(){
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();

        Billing billing = OrderTestDataBuilder.aBilling();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeBilling(billing))
                .withMessage(String.format(ERROR_ORDER_CANNOT_BE_EDITED,order.id(), order.status()));
    }

    @Test
    public void givenNotDraftOrder_whenTryChangeShipping_shouldGenerateException(){
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();

        Shipping shipping = OrderTestDataBuilder.aShipping();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changeShipping(shipping))
                .withMessage(String.format(ERROR_ORDER_CANNOT_BE_EDITED,order.id(), order.status()));
    }

    @Test
    public void givenNotDraftOrder_whenTryChangePaymentMethod_shouldGenerateException(){
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();

        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changePaymentMethod(creditCard, new CreditCardId()))
                .withMessage(String.format(ERROR_ORDER_CANNOT_BE_EDITED,order.id(), order.status()));
    }

    @Test
    public void givenDraftOrder_whenChangeStatusTryChangePaymentMethod_shouldGenerateException(){

        Order order = OrderTestDataBuilder.anOrder().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod creditCard = PaymentMethod.CREDIT_CARD;
        Product product = ProductTestDataBuilder.aProduct().build();

        order.addItem(product, new Quantity(1));
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(creditCard, new CreditCardId());


        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.isDraft()).isTrue(),
                o -> Assertions.assertThat(o.items()).isNotEmpty(),
                o -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                o -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(o.paymentMethod()).isEqualTo(creditCard)
        );

        order.place();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(() -> order.changePaymentMethod(PaymentMethod.GATEWAY_BALANCE, null))
                .withMessage(String.format(ERROR_ORDER_CANNOT_BE_EDITED,order.id(), order.status()));
    }

}
