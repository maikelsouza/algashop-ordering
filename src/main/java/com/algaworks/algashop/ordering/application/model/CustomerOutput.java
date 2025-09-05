package com.algaworks.algashop.ordering.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerOutput {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String document;

    private String phone;

    private LocalDate birthDate;

    private Boolean promotionNotificationsAllowed;

    private Integer loyaltyPoints;

    private OffsetDateTime registerAt;

    private OffsetDateTime archivedAt;

    private Boolean archived;

    private AddressData address;


}
