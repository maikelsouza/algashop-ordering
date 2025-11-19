package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItemId;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityDisassembler {

    public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity persistenceEntity){
        if (persistenceEntity == null) return null;
        return ShoppingCart.existing()
                .id(new ShoppingCartId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .createdAt(persistenceEntity.getCreatedAt())
                .items(buildItems(persistenceEntity.getItems()))
                .build();
    }

    private Set<ShoppingCartItem> buildItems(Set<ShoppingCartItemPersistenceEntity> items) {
        if (items == null || items.isEmpty()) {
            return new HashSet<>();
        }
        return items.stream().map(
                s ->
                        ShoppingCartItem.existing()
                                .id(new ShoppingCartItemId(s.getId()))
                                .shoppingCartId(new ShoppingCartId(s.getShoppingCartId()))
                                .quantity(new Quantity(s.getQuantity()))
                                .productId(new ProductId(s.getProductId()))
                                .productName(new ProductName(s.getName()))
                                .price(new Money(s.getPrice()))
                                .available(s.getAvailable())
                                .totalAmount(new Money(s.getTotalAmount()))
                                .build()
        ).collect(Collectors.toSet());
    }
}
