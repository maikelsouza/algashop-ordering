package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;

import static com.algaworks.algashop.ordering.domain.model.ErrorMessages.ERROR_CANT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY;

public class CantAddLoyaltyPointsOrderIsNotReadyException extends DomainException {


    public CantAddLoyaltyPointsOrderIsNotReadyException(OrderId id) {
        super(String.format(ERROR_CANT_ADD_LOYALTY_POINTS_ORDER_IS_NOT_READY,id));
    }
}
