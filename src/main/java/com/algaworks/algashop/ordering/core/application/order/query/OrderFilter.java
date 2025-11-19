package com.algaworks.algashop.ordering.core.application.order.query;

import com.algaworks.algashop.ordering.core.application.utility.SortablePageFilter;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderFilter extends SortablePageFilter<OrderFilter.SortType> {

    private String status;

    private String orderId;

    private UUID customerId;

    private OffsetDateTime placedAtFrom;

    private OffsetDateTime placedAtTo;

    private BigDecimal totalAmountFrom;

    private BigDecimal totalAmountTo;

    public OrderFilter(int size, int page) {
        super(size, page);
    }


    @Override
    public OrderFilter.SortType getSortByPropertyOrDefault() {
        return getSortByProperty() == null ? SortType.PLACED_AT : getSortByProperty() ;
    }

    @Override
    public Sort.Direction getSortDirectionOrDefault() {
        return getSortDirection() == null ? Sort.Direction.ASC : getSortDirection();
    }

    @RequiredArgsConstructor
    @Getter
    public enum SortType{
        PLACED_AT("placedAt"),
        PAID_AT("paidAt"),
        CANCEL_AT("cancelAt"),
        READY_AT("readyAt"),
        STATUS("status");

        private final String propertyName;


    }
}
