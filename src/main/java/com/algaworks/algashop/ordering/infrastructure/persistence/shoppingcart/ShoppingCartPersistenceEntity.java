package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = "id")
@Table(name = "shopping_cart")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class ShoppingCartPersistenceEntity extends AbstractAggregateRoot<ShoppingCartPersistenceEntity> {


    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;

    private BigDecimal totalAmount;

    private Integer totalItems;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItemPersistenceEntity> items = new HashSet<>();

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

    @Builder
    public ShoppingCartPersistenceEntity(UUID id, CustomerPersistenceEntity customer, BigDecimal totalAmount, Integer totalItems,
                                         Set<ShoppingCartItemPersistenceEntity> items, UUID createdByUserId,
                                         OffsetDateTime createdAt, OffsetDateTime lastModifiedAt,
                                         UUID lastModifiedByUserId, Long version) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.totalItems = totalItems;
        this.items = items;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.lastModifiedByUserId = lastModifiedByUserId;
        this.version = version;
        this.replaceItems(items);
    }

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> items){
        if(items == null || items.isEmpty()){
            this.setItems(new HashSet<>());
            return;
        }
        items.forEach(i -> i.setShoppingCart(this));
        this.setItems(items);
    }
    public UUID getCustomerId(){
        if (this.customer == null) return null;
        return this.customer.getId();
    }

    public void addEvents(Collection< Object > events) {
        if (events != null) {
            for (Object event : events) {
                this.registerEvent(event);
            }
        }
    }

}
