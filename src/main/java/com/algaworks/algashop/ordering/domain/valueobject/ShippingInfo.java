package com.algaworks.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

public record ShippingInfo(FullName fullName, Document document, Phone phone, Address address) {


    @Builder(toBuilder = true)
    public ShippingInfo {
        Objects.requireNonNull(fullName);
        Objects.requireNonNull(document);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(address);
    }

    @Override
    public String toString() {
        return fullName.toString() + " " + document.toString() + " " + phone.toString() + " " + address.toString();
    }
}
