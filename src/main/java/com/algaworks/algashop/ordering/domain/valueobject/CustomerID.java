package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.utility.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record CustomerID(UUID value) {

    public CustomerID() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    public CustomerID(UUID value){
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
