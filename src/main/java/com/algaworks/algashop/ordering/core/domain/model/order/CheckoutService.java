package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.DomainService;
import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@DomainService
@RequiredArgsConstructor
public class CheckoutService {

    private final CustomerHaveFreeShippingSpecification haveFreeShippingSpecification;

    public Order checkout(Customer customer, ShoppingCart shoppingCart,
                          Billing billing,
                          Shipping shipping,
                          PaymentMethod paymentMethod,
                          CreditCardId creditCardId){

        Objects.requireNonNull(customer);
        Objects.requireNonNull(shoppingCart);
        Objects.requireNonNull(billing);
        Objects.requireNonNull(shipping);
        Objects.requireNonNull(paymentMethod);

        if (shoppingCart.containsUnavailableItems() || shoppingCart.isEmpty()){
            throw new ShoppingCartCantProceedToCheckoutException(shoppingCart.id());
        }

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);

        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        }else{
            order.changeShipping(shipping);
        }

        order.changePaymentMethod(paymentMethod, creditCardId);

        shoppingCart.items().forEach(item ->
                order.addItem(new Product(item.productId(), item.productName(),
                        item.price(), item.isAvailable()), item.quantity()));

        order.place();
        shoppingCart.empty();
        return order;
    }

    private boolean haveFreeShipping(Customer customer){
        return haveFreeShippingSpecification.isSatisfiedBy(customer);
    }


}
