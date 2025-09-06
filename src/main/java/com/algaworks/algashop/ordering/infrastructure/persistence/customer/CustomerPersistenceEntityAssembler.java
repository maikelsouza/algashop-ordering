package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityAssembler {

    public CustomerPersistenceEntity fromDomain(Customer customer){
        return merge(new CustomerPersistenceEntity(), customer);
    }

    public CustomerPersistenceEntity merge(CustomerPersistenceEntity customerPersistenceEntity, Customer customer){
        customerPersistenceEntity.setId(customer.id().value());
        customerPersistenceEntity.setArchived(customer.isArchived());
        customerPersistenceEntity.setArchivedAt(customer.archivedAt());
        customerPersistenceEntity.setRegisteredAt(customer.registeredAt());
        customerPersistenceEntity.setDocument(customer.document().value());
        customerPersistenceEntity.setEmail(customer.email().value());
        customerPersistenceEntity.setBirthDate(customer.birthDate() != null ? customer.birthDate().value() : null);
        customerPersistenceEntity.setPhone(customer.phone().value());
        customerPersistenceEntity.setFirstName(customer.fullName().firstName());
        customerPersistenceEntity.setLastName(customer.fullName().lastName());
        customerPersistenceEntity.setLoyaltyPoints(customer.loyaltyPoints().value());
        customerPersistenceEntity.setPromotionNotificationsAllowed(customer.isPromotionNotificationsAllowed());
        customerPersistenceEntity.setVersion(customer.version());
        customerPersistenceEntity.setAddress(buildAddressEmbeddable(customer.address()));
        return customerPersistenceEntity;
    }

    public static AddressEmbeddable buildAddressEmbeddable(Address address){
        if (address == null) return null;
        return AddressEmbeddable.builder()
                .state(address.state())
                .city(address.city())
                .neighborhood(address.neighborhood())
                .complement(address.complement())
                .number(address.number())
                .street(address.street())
                .zipCode(address.zipCode().value())
                .build();
    }
}
