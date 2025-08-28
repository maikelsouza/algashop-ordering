package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.ShippingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
        orderPersistenceEntity.setBilling(buildBilling(order.billing()));
        orderPersistenceEntity.setShipping(buildShipping(order.shipping()));

        return orderPersistenceEntity;
    }

    private BillingEmbeddable buildBilling(Billing billing) {
        Objects.requireNonNull(billing);
        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .email(billing.email().value())
                .address(buildAddress(billing.address()))
                .build();
    }

    private AddressEmbeddable buildAddress(Address address){
        Objects.requireNonNull(address);
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

    private ShippingEmbeddable buildShipping(Shipping shipping){
        Objects.requireNonNull(shipping);
        return ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .address(buildAddress(shipping.address()))
                .expectedDate(shipping.expectedDate())
                .recipient(buildRecipient(shipping.recipient()))
                .build();

    }

    private RecipientEmbeddable buildRecipient(Recipient recipient) {
        Objects.requireNonNull(recipient);
        return RecipientEmbeddable.builder()
                .document(recipient.document().value())
                .firstName(recipient.fullName().firstName())
                .lastName(recipient.fullName().lastName())
                .phone(recipient.phone().value())
                .build();
    }
}
