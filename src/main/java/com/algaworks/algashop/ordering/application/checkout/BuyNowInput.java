package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.order.query.BillingData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyNowInput {

    @NotNull
    @Valid
    private ShippingInput shipping;

    @NotNull
    @Valid
    private BillingData billing;

    @NotNull
    private UUID productId;

    @NotNull
    private UUID customerId;

    @NotNull
    @Positive
    private Integer quantity;

    @NotBlank
    private String paymentMethod;


}
