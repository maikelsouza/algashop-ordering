package com.algaworks.algashop.ordering.infrastructure.persistence.embeddable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RecipientEmbeddable {

    private String firstName;

    private String lastName;

    private String document;

    private String phone;
}
