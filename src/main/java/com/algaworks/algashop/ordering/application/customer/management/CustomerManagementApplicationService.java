package com.algaworks.algashop.ordering.application.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.utility.Mapper;
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

    private final Mapper mapper;

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
                        .orElseThrow(() -> new CustomerNotFoundException(new CustomerId(customerId)));
        return mapper.covert(customer, CustomerOutput.class);
    }

    @Transactional
    public void update(UUID rawCustomerId, CustomerUpdateInput input){
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(input);

        Customer customer = customers.ofId(new CustomerId(rawCustomerId)).orElseThrow(
                () -> new CustomerNotFoundException(new CustomerId(rawCustomerId)));

        customer.changeName(new FullName(input.getFirstName(), input.getLastName()));
        customer.changePhone(new Phone(input.getPhone()));

        if (Boolean.TRUE.equals(input.getPromotionNotificationsAllowed())){
            customer.enablePromotionNotifications();
        } else{
            customer.disablePromotionNotifications();
        }

        AddressData address = input.getAddress();
        customer.changeAddress( Address.builder()
                    .zipCode(new ZipCode(address.getZipCode()))
                    .state(address.getState())
                    .city(address.getCity())
                    .neighborhood(address.getNeighborhood())
                    .street(address.getStreet())
                    .number(address.getNumber())
                    .complement(address.getComplement())
                    .build());

        customers.add(customer);
    }

}


