package com.algaworks.algashop.ordering.core.domain.model.order;

import com.algaworks.algashop.ordering.core.domain.model.commons.*;
import lombok.Builder;

import java.util.Objects;

public record Billing(FullName fullName, Document document, Phone phone, Email email, Address address) {

    @Builder(toBuilder = true)
    public Billing {
        Objects.requireNonNull(fullName);
        Objects.requireNonNull(document);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(email);
        Objects.requireNonNull(address);
    }

    @Override
    public String toString() {
        return fullName.toString() + " " + document.toString() + " " + phone.toString() + " " + email.toString() + " " + address.toString();
    }
}
