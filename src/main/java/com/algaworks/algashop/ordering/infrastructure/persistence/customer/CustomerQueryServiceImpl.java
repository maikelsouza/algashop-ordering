package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final EntityManager entityManager;

    private static final String findByIdAsOutputJPQL = """                      
            SELECT new com.algaworks.algashop.ordering.application.customer.query.CustomerOutput(
                c.id,
                c.firstName,
                c.lastName,
                c.email,
                c.document,
                c.phone,
                c.birthDate,
                c.loyaltyPoints,
                c.registeredAt,
                c.archivedAt,
                c.promotionNotificationsAllowed,
                c.archived,
                new com.algaworks.algashop.ordering.application.commons.AddressData(
                    c.address.street,
                    c.address.number,
                    c.address.complement,
                    c.address.neighborhood,
                    c.address.city,
                    c.address.state,
                    c.address.zipCode
                )
            )
            FROM CustomerPersistenceEntity c
            WHERE c.id = :id""";

    @Override
    public CustomerOutput findById(UUID customerId) {
        Objects.requireNonNull(customerId);
        try {
            TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
            query.setParameter("id", customerId);
            return query.getSingleResult();
        } catch (NoResultException e){
            throw new CustomerNotFoundException(new CustomerId(customerId));
        }
    }

    @Override
    public Page<CustomerSummaryOutput> filter(CustomerFilter filter) {
        Long totalQueryResults = countTotalQueryResults(filter);
        if (totalQueryResults.equals(0L)){
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            return new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResults);
        }
        return filterQuery(filter, totalQueryResults);
    }

    private Page<CustomerSummaryOutput> filterQuery(CustomerFilter filter, Long totalQueryResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerSummaryOutput> criteriaQuery = builder.createQuery(CustomerSummaryOutput.class);
        Root<CustomerPersistenceEntity> root = criteriaQuery.from(CustomerPersistenceEntity.class);
        criteriaQuery.select(
                builder.construct(CustomerSummaryOutput.class,
                        root.get("id"),
                        root.get("firstName"),
                        root.get("lastName"),
                        root.get("email"),
                        root.get("document"),
                        root.get("phone"),
                        root.get("birthDate"),
                        root.get("loyaltyPoints"),
                        root.get("registeredAt"),
                        root.get("archivedAt"),
                        root.get("promotionNotificationsAllowed"),
                        root.get("archived")
                )
        );

        Predicate[] predicates = toPredicates(builder, root, filter);
        Order sortOrder = toSorteCustomer(builder, root, filter);
        criteriaQuery.where(predicates);
        if (sortOrder != null){
            criteriaQuery.orderBy(sortOrder);
        }

        TypedQuery<CustomerSummaryOutput> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(filter.getSize() * filter.getPage());
        query.setMaxResults(filter.getSize());
        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResults);
    }

    private Order toSorteCustomer(CriteriaBuilder builder, Root<CustomerPersistenceEntity> root, CustomerFilter filter) {

        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC){
            return builder.asc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC){
            return builder.desc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        return null;
    }

    private Long countTotalQueryResults(CustomerFilter filter) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<CustomerPersistenceEntity> root = criteriaQuery.from(CustomerPersistenceEntity.class);
        Expression<Long> count = builder.count(root);

        Predicate[] predicates = toPredicates(builder, root, filter);
        criteriaQuery.select(count);
        criteriaQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    private Predicate[] toPredicates(CriteriaBuilder builder, Root<CustomerPersistenceEntity> root, CustomerFilter filter ){
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getEmail() != null){
            predicates.add(builder.equal(builder.upper(root.get("email")), filter.getEmail().toUpperCase()));
        }

        if (filter.getFirstName() != null){
            predicates.add(builder.like(builder.upper(root.get("firstName")), "%" + filter.getFirstName().toUpperCase() + "%"));
        }

        return predicates.toArray(new Predicate[]{});
    }

}
