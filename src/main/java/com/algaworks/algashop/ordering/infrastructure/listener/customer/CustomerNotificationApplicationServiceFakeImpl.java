package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.application.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerNotificationApplicationServiceFakeImpl implements CustomerNotificationApplicationService {

    private final Customers customers;

    @Override
    public void notifyNewRegistration(UUID customerId) {
        CustomerId customerIdObj = new CustomerId(customerId);
        Customer customer = customers.ofId(customerIdObj)
                .orElseThrow(() -> new CustomerNotFoundException(customerIdObj));

        log.info("Welcome {}", customer.fullName().firstName());
        log.info("User you email to access your account  {}", customer.email());
    }
}
