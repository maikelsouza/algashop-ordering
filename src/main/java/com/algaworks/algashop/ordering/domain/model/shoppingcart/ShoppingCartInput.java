package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ShoppingCartInput {

    @NotNull
    private UUID customerId;
}
