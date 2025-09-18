package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.order.*;
import com.algaworks.algashop.ordering.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {

    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity){
        if (persistenceEntity== null) return null;
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
                p ->
                        OrderItem.existing()
                                .id(new OrderItemId(p.getId()))
                                .orderId(new OrderId(p.getOrderId()))
                                .quantity(new Quantity(p.getQuantity()))
                                .productId(new ProductId(p.getProductId()))
                                .productName(new ProductName(p.getProductName()))
                                .price(new Money(p.getPrice()))
                                .totalAmount(new Money(p.getTotalAmount()))
                                .build()
        ).collect(Collectors.toSet());
    }



    private Billing buildBilling(BillingEmbeddable billingEmbeddable) {
        if (billingEmbeddable == null) return null;
        return Billing.builder()
                .fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
                .document(new Document(billingEmbeddable.getDocument()))
                .phone(new Phone(billingEmbeddable.getPhone()))
                .email(new Email(billingEmbeddable.getEmail()))
                .address(buildAddress(billingEmbeddable.getAddress()))
                .build();
    }

    private Address buildAddress(AddressEmbeddable addressEmbeddable){
        if (addressEmbeddable == null) return null;
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
        if (shippingEmbeddable == null) return null;
        return Shipping.builder()
                .cost(new Money(shippingEmbeddable.getCost()))
                .address(buildAddress(shippingEmbeddable.getAddress()))
                .expectedDate(shippingEmbeddable.getExpectedDate())
                .recipient(buildRecipient(shippingEmbeddable.getRecipient()))
                .build();
    }

    private Recipient buildRecipient(RecipientEmbeddable recipientEmbeddable) {
        if (recipientEmbeddable == null) return null;
        return Recipient.builder()
                .document(new Document(recipientEmbeddable.getDocument()))
                .fullName(new FullName(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
                .phone(new Phone(recipientEmbeddable.getPhone()))
                .build();
    }


}
