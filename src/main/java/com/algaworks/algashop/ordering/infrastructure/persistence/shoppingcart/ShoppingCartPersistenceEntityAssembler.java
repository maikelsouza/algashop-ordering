package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {

    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart){
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);
    }

    public ShoppingCartItemPersistenceEntity fromDomain(ShoppingCartItem shoppingCartItem) {
        return merge(new ShoppingCartItemPersistenceEntity(), shoppingCartItem);
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity shoppingCartPersistenceEntity, ShoppingCart shoppingCart){
        shoppingCartPersistenceEntity.setId(shoppingCart.id().value());
        shoppingCartPersistenceEntity.setTotalItems(shoppingCart.totalItems().value());
        shoppingCartPersistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        shoppingCartPersistenceEntity.setCreatedAt(shoppingCart.createdAt());
        Set<ShoppingCartItemPersistenceEntity> mergeItems = mergeItems(shoppingCart, shoppingCartPersistenceEntity);
        shoppingCartPersistenceEntity.replaceItems(mergeItems);
        CustomerPersistenceEntity customerPersistenceEntity = customerPersistenceEntityRepository.getReferenceById(shoppingCart.customerId().value());
        shoppingCartPersistenceEntity.setCustomer(customerPersistenceEntity);
        shoppingCartPersistenceEntity.addEvents(shoppingCart.domainEvents());
        return shoppingCartPersistenceEntity;
    }

    private ShoppingCartItemPersistenceEntity merge(ShoppingCartItemPersistenceEntity shoppingCartItemPersistenceEntity, ShoppingCartItem shoppingCartItem){
          shoppingCartItemPersistenceEntity.setId(shoppingCartItem.id().value());
          shoppingCartItemPersistenceEntity.setAvailable(shoppingCartItem.isAvailable());
          shoppingCartItemPersistenceEntity.setQuantity(shoppingCartItem.quantity().value());
          shoppingCartItemPersistenceEntity.setPrice(shoppingCartItem.price().value());
          shoppingCartItemPersistenceEntity.setProductId(shoppingCartItem.productId().value());
          shoppingCartItemPersistenceEntity.setTotalAmount(shoppingCartItem.totalAmount().value());
        shoppingCartItemPersistenceEntity.setName(shoppingCartItem.productName().value());
        return shoppingCartItemPersistenceEntity;
    }

    private Set<ShoppingCartItemPersistenceEntity> mergeItems(ShoppingCart shoppingCart, ShoppingCartPersistenceEntity shoppingCartPersistenceEntity){
        Set<ShoppingCartItem> newOrUpdateItems = shoppingCart.items();
        if (newOrUpdateItems == null || shoppingCart.items().isEmpty()){
            return new HashSet<>();
        }
        Set<ShoppingCartItemPersistenceEntity> existingItems = shoppingCartPersistenceEntity.getItems();

        if(existingItems == null || existingItems.isEmpty()){
            return newOrUpdateItems.stream().map(this::fromDomain).collect(Collectors.toSet());
        }
        Map<UUID, ShoppingCartItemPersistenceEntity> existingMap = existingItems.stream()
                .collect(Collectors.toMap(ShoppingCartItemPersistenceEntity::getId, item -> item));

        return newOrUpdateItems.stream()
                .map(shoppingCartItem -> {
                    ShoppingCartItemPersistenceEntity itemPersistence = existingMap.getOrDefault(
                            shoppingCartItem.id().value(), new ShoppingCartItemPersistenceEntity()
                    );
                    return merge(itemPersistence, shoppingCartItem);
                }).collect(Collectors.toSet());
    }
}
