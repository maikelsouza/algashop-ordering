package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityAssembler{

    public OrderPersistenceEntity fromDomain(Order order){
        return merge(new OrderPersistenceEntity(), order);
    }

    public OrderPersistenceEntity merge(OrderPersistenceEntity orderPersistenceEntity, Order order){
        orderPersistenceEntity.setId(order.id().value().toLong());
        orderPersistenceEntity.setCustomerId(order.customerId().value());
        orderPersistenceEntity.setTotalAmount(order.totalAmount().value());
        orderPersistenceEntity.setTotalItems(order.totalItems().value());
        orderPersistenceEntity.setStatus(order.status().name());
        orderPersistenceEntity.setPaymentMethod(order.paymentMethod().name());
        orderPersistenceEntity.setPlacedAt(order.placedAt());
        orderPersistenceEntity.setPaidAt(order.paidAt());
        orderPersistenceEntity.setCancelAt(order.canceledAt());
        orderPersistenceEntity.setReadyAt(order.readyAt());
        orderPersistenceEntity.setVersion(order.version());
        orderPersistenceEntity.setBilling(buildBillingEmbeddable(order.billing()));
        orderPersistenceEntity.setShipping(buildShippingEmbeddable(order.shipping()));
        Set<OrderItemPersistenceEntity> mergeItems = mergeItems(order, orderPersistenceEntity);
        orderPersistenceEntity.replaceItems(mergeItems);
        return orderPersistenceEntity;
    }

    public OrderItemPersistenceEntity fromDomain(OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    private OrderItemPersistenceEntity merge(OrderItemPersistenceEntity orderItemPersistenceEntity, OrderItem orderItem){
        orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.productId().value());
        orderItemPersistenceEntity.setProductName(orderItem.productName().value());
        orderItemPersistenceEntity.setPrice(orderItem.price().value());
        orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());
        return orderItemPersistenceEntity;
    }


    private Set<OrderItemPersistenceEntity> mergeItems(Order order, OrderPersistenceEntity orderPersistenceEntity){
        Set<OrderItem> newOrUpdateItems = order.items();
        if (newOrUpdateItems == null || order.items().isEmpty()){
            return new HashSet<>();
        }
        Set<OrderItemPersistenceEntity> existingItems = orderPersistenceEntity.getItems();

        if(existingItems == null || existingItems.isEmpty()){
            return newOrUpdateItems.stream().map(this::fromDomain).collect(Collectors.toSet());
        }
        Map<Long, OrderItemPersistenceEntity> existingMap = existingItems.stream()
                .collect(Collectors.toMap(OrderItemPersistenceEntity::getId, item -> item));

        return newOrUpdateItems.stream()
                .map(orderItem -> {
                    OrderItemPersistenceEntity itemPersistence = existingMap.getOrDefault(
                            orderItem.id().value().toLong(), new OrderItemPersistenceEntity()
                    );
                    return merge(itemPersistence, orderItem);
                }).collect(Collectors.toSet());
    }



    private BillingEmbeddable buildBillingEmbeddable(Billing billing) {
        if (billing == null) return null;
        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .email(billing.email().value())
                .address(buildAddressEmbeddable(billing.address()))
                .build();
    }

    private AddressEmbeddable buildAddressEmbeddable(Address address){
        if (address == null) return null;
        return AddressEmbeddable.builder()
                .state(address.state())
                .city(address.city())
                .neighborhood(address.neighborhood())
                .complement(address.complement())
                .number(address.number())
                .street(address.street())
                .zipCode(address.zipCode().value())
                .build();
    }

    private ShippingEmbeddable buildShippingEmbeddable(Shipping shipping){
        if (shipping == null) return null;
        return ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .address(buildAddressEmbeddable(shipping.address()))
                .expectedDate(shipping.expectedDate())
                .recipient(buildRecipientEmbeddable(shipping.recipient()))
                .build();
    }

    private RecipientEmbeddable buildRecipientEmbeddable(Recipient recipient) {
        if (recipient == null) return null;
        return RecipientEmbeddable.builder()
                .document(recipient.document().value())
                .firstName(recipient.fullName().firstName())
                .lastName(recipient.fullName().lastName())
                .phone(recipient.phone().value())
                .build();
    }
}
