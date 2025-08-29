package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {

    public Customer toDomainEntity(CustomerPersistenceEntity persistenceEntity) {
        return Customer.existing()
                .id(new CustomerId(persistenceEntity.getId()))
                .email(new Email(persistenceEntity.getEmail()))
                .phone(new Phone(persistenceEntity.getPhone()))
                .birthDate(new BirthDate(persistenceEntity.getBirthDate()))
                .address(buildAddress(persistenceEntity.getAddress()))
                .document(new Document(persistenceEntity.getDocument()))
                .promotionNotificationsAllowed(persistenceEntity.getPromotionNotificationsAllowed())
                .archived(persistenceEntity.getArchived())
                .archivedAt(persistenceEntity.getArchivedAt())
                .registeredAt(persistenceEntity.getRegisteredAt())
                .fullName(new FullName(persistenceEntity.getFirstName(), persistenceEntity.getLastName()))
                .loyaltyPoints(new LoyaltyPoints(persistenceEntity.getLoyaltyPoints()))
                .version(persistenceEntity.getVersion())
                .build();
    }

    public static Address buildAddress(AddressEmbeddable addressEmbeddable){
        if (addressEmbeddable == null) return null;
        return Address.builder()
                .state(addressEmbeddable.getState())
                .city(addressEmbeddable.getCity())
                .neighborhood(addressEmbeddable.getNeighborhood())
                .complement(addressEmbeddable.getComplement())
                .number(addressEmbeddable.getNumber())
                .street(addressEmbeddable.getStreet())
                .zipCode(new ZipCode(addressEmbeddable.getZipCode()))
                .build();
    }
}
