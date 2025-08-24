package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.*;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.LocalDate;

public class OrderTestDataBuilder {


    private CustomerId customerId = new CustomerId();

    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

    private static Shipping shipping = aShipping();

    public static Billing billing = aBilling();

    private boolean withItems = true;

    private OrderStatus status = OrderStatus.DRAFT;

    public OrderTestDataBuilder() {
    }

    public static OrderTestDataBuilder anOrder(){
        return new OrderTestDataBuilder();
    }

    public Order build(){
        Order order = Order.draft(customerId);
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod);

        if (withItems){
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(2));

            order.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));
        }

        switch (this.status){
            case DRAFT -> {}
            case PLACED -> {
                order.place();
            }
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {
                order.place();
                order.markAsPaid();
                order.markAsReady();
            }
            case CANCELED-> {
                order.cancel();
            }
        }
        return order;
    }

    public static Shipping aShipping() {
        return  Shipping.builder()
                .cost(new Money("10"))
                .expectedDate(LocalDate.now().plusWeeks(1))
                .address(anAddress())
                .recipient(Recipient.builder()
                        .fullName(new FullName("John", "Doe"))
                        .document(new Document("112-33-2321"))
                        .phone(new Phone("111-441-1244"))
                        .build())
                .build();
    }

    public static Shipping aShippingAlt() {
        return  Shipping.builder()
                .cost(new Money("20"))
                .expectedDate(LocalDate.now().plusWeeks(2))
                .address(anAddressAlt())
                .recipient(Recipient.builder()
                        .fullName(new FullName("Mary", "Jones"))
                        .document(new Document("552-11-4333"))
                        .phone(new Phone("54-454-1144"))
                        .build())
                .build();
    }

    public static Billing aBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe"))
                .email(new Email("john.doe@gmail.com"))
                .build();
    }

    public static Address anAddress(){
        return Address.builder()
                .street("Bourbon Street")
                .number("1234")
                .neighborhood("North Ville")
                .complement("apt 11")
                .city("Montfort")
                .state("South Caroline")
                .zipCode(new ZipCode("79911"))
                .build();
    }

    public static Address anAddressAlt(){
        return Address.builder()
                .street("Sansome Street")
                .number("875")
                .neighborhood("Sansome")
                .complement("apt 11")
                .city("San Francisco")
                .state("California")
                .zipCode(new ZipCode("08040"))
                .build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public static void shipping(Shipping shipping) {
        OrderTestDataBuilder.shipping = shipping;
    }

    public static void billing(Billing billing) {
        OrderTestDataBuilder.billing = billing;
    }

    public OrderTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

}
