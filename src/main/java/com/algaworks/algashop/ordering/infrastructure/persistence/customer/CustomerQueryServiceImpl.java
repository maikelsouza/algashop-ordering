package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final Mapper mapper;

    private final Customers customers;

    private final CustomerPersistenceEntityRepository repository;

    @Override
    public CustomerOutput findById(UUID customerId) {
        Objects.requireNonNull(customerId);
        return repository.findByIdAsOutput(customerId)
                        .orElseThrow(() -> new CustomerNotFoundException(new CustomerId(customerId)));
    }
}
