package com.algaworks.algashop.ordering.application.service;

import com.algaworks.algashop.ordering.application.model.AddressData;
import com.algaworks.algashop.ordering.application.model.CustomerInput;
import com.algaworks.algashop.ordering.application.model.CustomerOutput;
import com.algaworks.algashop.ordering.domain.model.commons.*;
import com.algaworks.algashop.ordering.domain.model.customer.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {

    private final CustomerRegistrationService customerRegistration;

    private final Customers customers;

    @Transactional
    public UUID create(CustomerInput input){
        Objects.requireNonNull(input);
        AddressData address = input.getAddress();

        Customer customer = customerRegistration.register(
                new FullName(input.getFirstName(), input.getLastName()),
                new BirthDate(input.getBirthDate()),
                new Email(input.getEmail()),
                new Phone(input.getPhone()),
                new Document(input.getDocument()),
                input.getPromotionNotificationsAllowed(),
                Address.builder()
                        .zipCode(new ZipCode(address.getZipCode()))
                        .state(address.getState())
                        .city(address.getCity())
                        .neighborhood(address.getNeighborhood())
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .build()
        );
        customers.add(customer);
        return customer.id().value();
    }

    @Transactional(readOnly = true)
    public CustomerOutput findById(UUID customerId){
        Objects.requireNonNull(customerId);

        Customer customer = customers.ofId(new CustomerId(customerId))
                        .orElseThrow(() -> new CustomerNotFoundException(customerId));
        Address address = customer.address();
        return CustomerOutput.builder()
                .id(customer.id().value())
                .firstName(customer.fullName().firstName())
                .lastName(customer.fullName().lastName())
                .email(customer.email().value())
                .document(customer.document().value())
                .phone(customer.phone().value())
                .promotionNotificationsAllowed(customer.isPromotionNotificationsAllowed())
                .loyaltyPoints(customer.loyaltyPoints().value())
                .registerAt(customer.registeredAt())
                .birthDate(customer.birthDate() != null ? customer.birthDate().value() :null)
                .archived(customer.isArchived())
                .address(AddressData.builder()
                        .street(address.street())
                        .number(address.number())
                        .complement(address.complement())
                        .neighborhood(address.neighborhood())
                        .city(address.city())
                        .state(address.state())
                        .zipCode(address.zipCode().value())
                        .build())

                .build();
    }
}


