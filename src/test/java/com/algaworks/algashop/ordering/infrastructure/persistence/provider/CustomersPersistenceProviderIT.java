package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({CustomersPersistenceProvider.class, CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class, SpringDataAuditingConfig.class})
class CustomersPersistenceProviderIT {

    private CustomersPersistenceProvider persistenceProvider;

    private CustomerPersistenceEntityRepository entityRepository;

    @Autowired
    public CustomersPersistenceProviderIT(CustomersPersistenceProvider persistenceProvider, CustomerPersistenceEntityRepository entityRepository) {
        this.persistenceProvider = persistenceProvider;
        this.entityRepository = entityRepository;
    }

    @Test
    public void shouldSavedACustomerAndValidateValues() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        persistenceProvider.add(customer);

        CustomerPersistenceEntity savedCustomer = entityRepository.findById(customer.id().value()).orElseThrow();

        Assertions.assertThat(savedCustomer).satisfies(
                entity -> Assertions.assertThat(entity.getId()).isNotNull(),
                entity -> Assertions.assertThat(entity.getFirstName()).isEqualTo(customer.fullName().firstName()),
                entity -> Assertions.assertThat(entity.getLastName()).isEqualTo(customer.fullName().lastName()),
                entity -> Assertions.assertThat(entity.getBirthDate()).isEqualTo(customer.birthDate().value()),
                entity -> Assertions.assertThat(entity.getEmail()).isEqualTo(customer.email().value()),
                entity -> Assertions.assertThat(entity.getPhone()).isEqualTo(customer.phone().value()),
                entity -> Assertions.assertThat(entity.getDocument()).isEqualTo(customer.document().value()),
                entity -> Assertions.assertThat(entity.getPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed()),
                entity -> Assertions.assertThat(entity.getArchived()).isEqualTo(customer.isArchived()),
                entity -> Assertions.assertThat(entity.getRegisteredAt()).isEqualTo(customer.registeredAt()),
                entity -> Assertions.assertThat(entity.getAddress())
                        .isEqualTo(CustomerPersistenceEntityAssembler.buildAddressEmbeddable(customer.address()))
        );

    }

    @Test
    void shouldModifyNewEmail() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        persistenceProvider.add(customer);

        CustomerPersistenceEntity persistenceEntity = entityRepository.findById(customer.id().value()).orElseThrow();

        Assertions.assertThat(persistenceEntity.getEmail()).isNotNull();
        Assertions.assertThat(persistenceEntity.getEmail()).isEqualTo(customer.email().value());


        Customer savedCustomer = persistenceProvider.ofId(customer.id()).orElseThrow();

        Email email = new Email("john.doe@gmail.com");
        savedCustomer.changeEmail(email);

        persistenceProvider.add(savedCustomer);
        persistenceEntity = entityRepository.findById(customer.id().value()).orElseThrow();


        Assertions.assertThat(persistenceEntity).satisfies(
                entity -> Assertions.assertThat(entity.getEmail()).isNotNull(),
                entity -> Assertions.assertThat(entity.getEmail()).isEqualTo(email.value())
        );
    }


    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldAddFindNotFailWhenNotTransaction(){
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        persistenceProvider.add(customer);

        Assertions.assertThatNoException()
                .isThrownBy(() -> persistenceProvider.ofId(customer.id()).orElseThrow());
    }
}