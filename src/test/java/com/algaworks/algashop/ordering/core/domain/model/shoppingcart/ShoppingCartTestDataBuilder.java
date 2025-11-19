package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;

import static com.algaworks.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class ShoppingCartTestDataBuilder {

    public static final ShoppingCartId DEFAULT_SHOPPING_CART_ID = new ShoppingCartId();

    private boolean withItems = false;

    public CustomerId customerId = DEFAULT_CUSTOMER_ID;

    private ShoppingCartTestDataBuilder() {}

    public static ShoppingCartTestDataBuilder aShoppingCart() {
        return new ShoppingCartTestDataBuilder();
    }

    public ShoppingCart build() {
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customerId);

        if (withItems){
            shoppingCart.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));
            shoppingCart.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));
        }
        return shoppingCart;
    }

    public ShoppingCartTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public ShoppingCartTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

}
