package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;

    private final Mapper mapper;

    @Override
    public OrderDetailOutput findById(String id) {
        OrderId orderId = new OrderId(id);
        OrderPersistenceEntity entity = repository.findById(orderId.value().toLong())
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapper.covert(entity, OrderDetailOutput.class);
    }
}
