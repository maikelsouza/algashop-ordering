package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.*;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.application.utility.PageFilter;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderPersistenceEntityRepository repository;

    private final EntityManager entityManager;

    private final Mapper mapper;

    @Override
    public OrderDetailOutput findById(String id) {
        OrderId orderId = new OrderId(id);
        OrderPersistenceEntity entity = repository.findById(orderId.value().toLong())
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapper.covert(entity, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);
        if (totalQueryResults.equals(0L)){
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }
        return filterQuery(filter, totalQueryResults);
    }

    private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = builder.createQuery(OrderSummaryOutput.class);
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);
        Path<Object> customer = root.get("customer");
        criteriaQuery.select(
                builder.construct(OrderSummaryOutput.class,
                    root.get("id"),
                    builder.construct(CustomerMinimalOutput.class,
                            customer.get("id"),
                            customer.get("firstName"),
                            customer.get("lastName"),
                            customer.get("email"),
                            customer.get("document"),
                            customer.get("phone")
                    ),
                    root.get("totalItems"),
                    root.get("totalAmount"),
                    root.get("placedAt"),
                    root.get("paidAt"),
                    root.get("cancelAt"),
                    root.get("readyAt"),
                    root.get("status"),
                    root.get("paymentMethod")
                )
        );

        Predicate[] predicates = toPredicates(builder, root, filter);
        criteriaQuery.where(predicates);

        TypedQuery<OrderSummaryOutput> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(filter.getSize() * filter.getPage());
        query.setMaxResults(filter.getSize());
        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResults);
    }

    private Long countTotalQueryResults(OrderFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);
        Expression<Long> count = builder.count(root);

        Predicate[] predicates = toPredicates(builder, root, filter);
        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate[] toPredicates(CriteriaBuilder builder, Root<OrderPersistenceEntity> root, OrderFilter filter ){
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getCustomerId() != null){
            Path<Object> customerIdPath = root.get("customer").get("id");
            UUID expectedCustomerId = filter.getCustomerId();
            Predicate predicate = builder.equal(customerIdPath, expectedCustomerId);
            predicates.add(predicate);
        }
        return predicates.toArray(new Predicate[]{});
    }
}
