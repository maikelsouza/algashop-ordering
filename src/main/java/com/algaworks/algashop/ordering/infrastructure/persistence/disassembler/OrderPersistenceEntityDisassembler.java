package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity){
        return Order.existing()
                .id(new OrderId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .status(OrderStatus.valueOf(persistenceEntity.getStatus()))
                .paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
                .placedAt(persistenceEntity.getPlacedAt())
                .paidAt(persistenceEntity.getPaidAt())
                .canceledAt(persistenceEntity.getCancelAt())
                .readyAt(persistenceEntity.getReadyAt())
                .items(buildItems(persistenceEntity.getItems()))
                .version(persistenceEntity.getVersion())
                .billing(buildBilling(persistenceEntity.getBilling()))
                .shipping(buildShipping(persistenceEntity.getShipping()))
                .build();
    }

    private Set<OrderItem> buildItems(Set<OrderItemPersistenceEntity> items) {

        if(items == null || items.isEmpty()){
            return new HashSet<>();
        }
        return items.stream().map(
                i ->
                        OrderItem.brandNew()
                                .orderId(new OrderId(i.getOrderId()))
                                .quantity(new Quantity(i.getQuantity()))
                                .product(Product.builder()
                                        .id(new ProductId(i.getProductId()))
                                        .price(new Money(i.getPrice()))
                                        .inStock(true)
                                        .name(new ProductName(i.getProductName()))
                                        .build())
                                .build()
        ).collect(Collectors.toSet());




    }



    private Billing buildBilling(BillingEmbeddable billingEmbeddable) {
        Objects.requireNonNull(billingEmbeddable);
        return Billing.builder()
                .fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
                .document(new Document(billingEmbeddable.getDocument()))
                .phone(new Phone(billingEmbeddable.getPhone()))
                .email(new Email(billingEmbeddable.getEmail()))
                .address(buildAddress(billingEmbeddable.getAddress()))
                .build();
    }

    private Address buildAddress(AddressEmbeddable addressEmbeddable){
        Objects.requireNonNull(addressEmbeddable);
        return Address.builder()
                .state(addressEmbeddable.getState())
                .city(addressEmbeddable.getCity())
                .neighborhood(addressEmbeddable.getNeighborhood())
                .complement(addressEmbeddable.getComplement())
                .number(addressEmbeddable.getNumber())
                .street(addressEmbeddable.getStreet())
                .zipCode(new ZipCode(addressEmbeddable.getZipCode()))
                .build();
    }

    private Shipping buildShipping(ShippingEmbeddable shippingEmbeddable){
        Objects.requireNonNull(shippingEmbeddable);
        return Shipping.builder()
                .cost(new Money(shippingEmbeddable.getCost()))
                .address(buildAddress(shippingEmbeddable.getAddress()))
                .expectedDate(shippingEmbeddable.getExpectedDate())
                .recipient(buildRecipient(shippingEmbeddable.getRecipient()))
                .build();
    }

    private Recipient buildRecipient(RecipientEmbeddable recipientEmbeddable) {
        Objects.requireNonNull(recipientEmbeddable);
        return Recipient.builder()
                .document(new Document(recipientEmbeddable.getDocument()))
                .fullName(new FullName(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
                .phone(new Phone(recipientEmbeddable.getPhone()))
                .build();
    }


}
