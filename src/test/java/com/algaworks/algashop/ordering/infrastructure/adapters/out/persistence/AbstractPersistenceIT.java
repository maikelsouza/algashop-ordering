package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence;

import com.algaworks.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.utils.TestContainerPostgreSQLConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestContainerPostgreSQLConfig.class, SpringDataAuditingConfig.class})
public class AbstractPersistenceIT {
}
