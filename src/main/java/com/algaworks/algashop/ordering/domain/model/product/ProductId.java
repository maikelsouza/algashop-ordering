package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    public ProductId(UUID value){
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
