package com.algaworks.algashop.ordering.domain.model.valueobject;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

public record Shipping(Money cost, LocalDate expectedDate, Recipient recipient, Address address) {


    @Builder(toBuilder = true)
    public Shipping {
        Objects.requireNonNull(cost);
        Objects.requireNonNull(expectedDate);
        Objects.requireNonNull(recipient);
        Objects.requireNonNull(address);
    }

    @Override
    public String toString() {
        return cost + " "  + expectedDate + " " + recipient + " " + address;
    }
}
