package com.algaworks.algashop.ordering.application.order.query;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderItemId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderDetailOutputTestDataBuilder {

    public static OrderDetailOutput.OrderDetailOutputBuilder placedOrder() {
        return placedOrder(new OrderId().toString());
    }
    public static OrderDetailOutput.OrderDetailOutputBuilder placedOrder(String orderId) {
        return OrderDetailOutput.builder()
                .id(orderId)
                .customer(CustomerMinimalOutput.builder()
                        .id(new CustomerId().value())
                        .firstName("John")
                        .lastName("Doe")
                        .document("12345")
                        .email("johndoe@email.com")
                        .phone("1191234564")
                        .build())
                .totalItems(2)
                .totalAmount(new BigDecimal("41.98"))
                .placedAt(OffsetDateTime.now())
                .paidAt(null)
                .canceledAt(null)
                .readAt(null)
                .status("PLACED")
                .paymentMethod("GATEWAY_BALANCE")
                .shipping(ShippingData.builder()
                        .cost(new BigDecimal("20.5"))
                        .expectedDate(LocalDate.now().plusDays(2))
                        .recipient(RecipientData.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .document("12345")
                                .phone("5511912341234")
                                .build())
                        .address(AddressData.builder()
                                .street("Bourbon Street")
                                .number("2000")
                                .complement("apt 122")
                                .neighborhood("North Ville")
                                .city("Yostfort")
                                .state("South Carolina")
                                .zipCode("12321")
                                .build())
                        .build())
                .billing(BillingData.builder()
                        .firstName("John")
                        .lastName("Doe")
                        .document("12345")
                        .phone("5511912341234")
                        .address(AddressData.builder()
                                .street("Bourbon Street")
                                .number("2000")
                                .complement("apt 122")
                                .neighborhood("North Ville")
                                .city("Yostfort")
                                .state("South Carolina")
                                .zipCode("12321")
                                .build())
                        .build())
                .items(itemsOutput(orderId));
    }

    private static List itemsOutput(String orderId) {
        List items = new ArrayList<>();
        items.add(OrderItemDetailOutput.builder()
                .id(new OrderItemId().toString())
                .orderId(orderId)
                .productId(UUID.randomUUID())
                .productName("Notebook Dive Gamer X11")
                .price(new BigDecimal("19.99"))
                .quantity(2)
                .totalAmount(new BigDecimal("41.98"))
                .build());
        return items;
    }
}
