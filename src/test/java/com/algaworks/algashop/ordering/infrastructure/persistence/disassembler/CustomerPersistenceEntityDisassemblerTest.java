package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;


import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerPersistenceEntityDisassemblerTest {

    private final CustomerPersistenceEntityDisassembler disassembler = new CustomerPersistenceEntityDisassembler();

    @Test
    public void shouldConvertFromPersistence(){

        CustomerPersistenceEntity persistenceEntity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        Customer domainEntity = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(domainEntity).satisfies(
                c-> Assertions.assertThat(c.id()).isEqualTo(new CustomerId(persistenceEntity.getId())),
                c-> Assertions.assertThat(c.birthDate()).isEqualTo(new BirthDate(persistenceEntity.getBirthDate())),
                c-> Assertions.assertThat(c.archivedAt()).isEqualTo(persistenceEntity.getArchivedAt()),
                c-> Assertions.assertThat(c.isPromotionNotificationsAllowed()).isEqualTo(persistenceEntity.getPromotionNotificationsAllowed()),
                c-> Assertions.assertThat(c.isArchived()).isEqualTo(persistenceEntity.getArchived()),
                c-> Assertions.assertThat(c.document()).isEqualTo(new Document(persistenceEntity.getDocument())),
                c-> Assertions.assertThat(c.fullName()).isEqualTo(new FullName(persistenceEntity.getFirstName(), persistenceEntity.getLastName())),
                c-> Assertions.assertThat(c.address()).isEqualTo(CustomerPersistenceEntityDisassembler.buildAddress(persistenceEntity.getAddress()))
        );

    }
}