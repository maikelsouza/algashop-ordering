package com.algaworks.algashop.ordering.core.application.checkout;

import com.algaworks.algashop.ordering.core.domain.model.DomainException;
import com.algaworks.algashop.ordering.core.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.core.domain.model.order.*;
import com.algaworks.algashop.ordering.core.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.core.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {

    private final CheckoutService checkoutService;

    private final ShoppingCarts shoppingCarts;

    private final Orders orders;

    private final Customers customers;

    private final ShippingCostService shippingCostService;

    private final OriginAddressService originAddressService;

    private final BillingInputDisassembler billingInputDisassembler;
    private final ShippingInputDisassembler shippingInputDisassembler;

    @Transactional
    public String  checkout(CheckoutInput input){
        Objects.requireNonNull(input);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());

        CreditCardId creditCardId = null;

        if(paymentMethod.equals(PaymentMethod.CREDIT_CARD)){
            if(input.getCreditCardId() == null){
                throw new DomainException("Credit Card id is required");
            }
            creditCardId = new CreditCardId(input.getCreditCardId());
        }

        ShoppingCartId shoppingCartId = new ShoppingCartId(input.getShoppingCartId());
        ShoppingCart shoppingCart = shoppingCarts.ofId(shoppingCartId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCartId));

        Customer customer = customers.ofId(shoppingCart.customerId()).orElseThrow(
                () -> new CustomerNotFoundException(shoppingCart.customerId()));


        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode(input.getShipping().getAddress().getZipCode());

        ShippingCostService.CalculationResult calculate = shippingCostService.calculate(
                new ShippingCostService.CalculationRequest(origin, destination));

        Order order = checkoutService.checkout(customer, shoppingCart,
                billingInputDisassembler.toDomainModel(input.getBilling()),
                shippingInputDisassembler.toDomainModel(input.getShipping(), calculate),
                paymentMethod, creditCardId);

        orders.add(order);
        shoppingCarts.add(shoppingCart);

        return order.id().toString();
    }
}
