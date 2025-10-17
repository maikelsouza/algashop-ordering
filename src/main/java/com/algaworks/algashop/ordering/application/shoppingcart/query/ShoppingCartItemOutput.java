package com.algaworks.algashop.ordering.application.shoppingcart.query;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ShoppingCartItemOutput {

    private UUID id;

    private UUID productId;

    private String name;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalAmount;

    private Boolean available;
}
