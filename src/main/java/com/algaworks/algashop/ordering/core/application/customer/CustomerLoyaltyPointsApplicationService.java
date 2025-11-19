package com.algaworks.algashop.ordering.core.application.customer;

import com.algaworks.algashop.ordering.core.domain.model.customer.*;
import com.algaworks.algashop.ordering.core.domain.model.order.Order;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.core.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.core.domain.model.order.Orders;
import com.algaworks.algashop.ordering.core.ports.in.customer.ForAddingLoyaltyPoints;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService implements ForAddingLoyaltyPoints {

    private final Customers customers;

    private final Orders orders;

    private final CustomerLoyaltyPointsService customerLoyaltyPointsService;

    @Transactional
    @Override
    public void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId){

        Customer customer = customers.ofId(new CustomerId(rawCustomerId))
                .orElseThrow(() -> new CustomerNotFoundException(new CustomerId(rawCustomerId)));

        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException(new OrderId(rawOrderId)));

        customerLoyaltyPointsService.addPoints(customer, order);

        customers.add(customer);

    }
}
