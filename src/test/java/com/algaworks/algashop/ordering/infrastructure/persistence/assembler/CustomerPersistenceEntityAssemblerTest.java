package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;


import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.valueobject.*;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerPersistenceEntityAssemblerTest {


    private final CustomerPersistenceEntityAssembler assembler = new CustomerPersistenceEntityAssembler();

    @Test
    void shouldConvertToDomain(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        CustomerPersistenceEntity customerPersistenceEntity = assembler.fromDomain(customer);

        Assertions.assertThat(customerPersistenceEntity).satisfies(
          c-> Assertions.assertThat(c.getId()).isEqualTo(customer.id().value()),
          c-> Assertions.assertThat(c.getEmail()).isEqualTo(customer.email().value()),
          c-> Assertions.assertThat(c.getPhone()).isEqualTo(customer.phone().value()),
          c-> Assertions.assertThat(c.getBirthDate()).isEqualTo(customer.birthDate().value()),
          c-> Assertions.assertThat(c.getDocument()).isEqualTo(customer.document().value()),
          c-> Assertions.assertThat(c.getPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed()),
          c-> Assertions.assertThat(c.getArchived()).isEqualTo(customer.isArchived()),
          c-> Assertions.assertThat(c.getArchivedAt()).isEqualTo(customer.archivedAt()),
          c-> Assertions.assertThat(c.getRegisteredAt()).isEqualTo(customer.registeredAt()),
          c-> Assertions.assertThat(c.getFirstName()).isEqualTo(customer.fullName().firstName()),
          c-> Assertions.assertThat(c.getLastName()).isEqualTo(customer.fullName().lastName()),
          c-> Assertions.assertThat(c.getLoyaltyPoints()).isEqualTo(customer.loyaltyPoints().value()),
          c-> Assertions.assertThat(c.getVersion()).isEqualTo(customer.version()),
          c-> Assertions.assertThat(c.getAddress()).isEqualTo(CustomerPersistenceEntityAssembler.buildAddressEmbeddable(customer.address()))
        );
    }

}