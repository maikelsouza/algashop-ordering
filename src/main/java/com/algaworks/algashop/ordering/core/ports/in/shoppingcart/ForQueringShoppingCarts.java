package com.algaworks.algashop.ordering.core.ports.in.shoppingcart;

import java.util.UUID;

public interface ForQueringShoppingCarts {

    ShoppingCartOutput findById(UUID shoppingCartId);

    ShoppingCartOutput findByCustomerId(UUID customerId);
}
