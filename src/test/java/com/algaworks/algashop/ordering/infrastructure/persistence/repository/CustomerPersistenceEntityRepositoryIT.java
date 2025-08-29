package com.algaworks.algashop.ordering.infrastructure.persistence.repository;

import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SpringDataAuditingConfig.class)
public class CustomerPersistenceEntityRepositoryIT {


    private final CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @Autowired
    public CustomerPersistenceEntityRepositoryIT(CustomerPersistenceEntityRepository customerPersistenceEntityRepository) {
        this.customerPersistenceEntityRepository = customerPersistenceEntityRepository;
    }

    @Test
    public void shouldPersist(){
        CustomerPersistenceEntity entity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        customerPersistenceEntityRepository.saveAndFlush(entity);
        Assertions.assertThat(customerPersistenceEntityRepository.existsById(entity.getId())).isTrue();

        CustomerPersistenceEntity savedEntity = customerPersistenceEntityRepository.findById(entity.getId()).orElseThrow();

        Assertions.assertThat(savedEntity).isNotNull();
    }

    @Test
    public void shouldCount(){
        long count = customerPersistenceEntityRepository.count();

        Assertions.assertThat(count).isZero();
    }

    @Test
    public void shouldSetAuditingValues(){
        CustomerPersistenceEntity persistenceEntity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();
        persistenceEntity = customerPersistenceEntityRepository.saveAndFlush(persistenceEntity);

        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }
}
