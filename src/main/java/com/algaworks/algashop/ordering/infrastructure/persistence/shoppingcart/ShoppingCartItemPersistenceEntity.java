package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = "id")
@Table(name = "shopping_cart_item")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Builder
public class ShoppingCartItemPersistenceEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private UUID productId;

    private String productName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalAmount;

    private Boolean available;

    @JoinColumn
    @ManyToOne(optional = false)
    private ShoppingCartPersistenceEntity shoppingCart;

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;

    public UUID getShoppingCartId(){
        if (getShoppingCart() == null){
            return null;
        }
        return getShoppingCart().getId();
    }
}
