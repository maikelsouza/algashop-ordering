package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;

public class ShoppingCartItemTestDataBuilder {

    private ShoppingCartItemTestDataBuilder() {
    }

    public static ShoppingCartItemTestDataBuilder aShoppingCartItem(){
        return new ShoppingCartItemTestDataBuilder();
    }

    public ShoppingCartItem build() {
        return ShoppingCartItem.brandNew()
                .shoppingCartId(new ShoppingCartId())
                .productName(new ProductName("Notebook"))
                .productId(new ProductId())
                .price(new Money("1000"))
                .available(true)
                .quantity(new Quantity(1))
                .build();
    }
}
