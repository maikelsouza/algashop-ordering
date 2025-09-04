package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import static com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

public class ShoppingCartTestDataBuilder {

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
