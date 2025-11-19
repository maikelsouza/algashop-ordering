package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.core.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.core.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.core.application.utility.Mapper;
import com.algaworks.algashop.ordering.core.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {

    private final ShoppingCartPersistenceEntityRepository repository;

    private final Mapper mapper;


    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        ShoppingCartId shoppingCart = new ShoppingCartId(shoppingCartId);
        ShoppingCartPersistenceEntity entity = repository.findById(shoppingCart.value())
                .orElseThrow(() -> new ShoppingCartNotFoundException(shoppingCart));
        return mapper.convert(entity, ShoppingCartOutput.class);
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        ShoppingCartPersistenceEntity entity = repository.findByCustomer_Id(customerId)
                .orElseThrow(() -> new ShoppingCartNotFoundException(new CustomerId(customerId)));
        return mapper.convert(entity, ShoppingCartOutput.class);
    }

}




