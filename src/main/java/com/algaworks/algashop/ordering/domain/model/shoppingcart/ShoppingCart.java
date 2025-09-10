package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.AbstractEventSourceEntity;
import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

public class ShoppingCart extends AbstractEventSourceEntity implements AggregateRoot<ShoppingCartId> {

    private ShoppingCartId id;

    private CustomerId customerId;

    private Money totalAmount;

    private Quantity totalItems;

    private OffsetDateTime createdAt;

    private Set<ShoppingCartItem> items;

    private Long version;


    @Builder(builderClassName = "ExistingShoppingCartBuilder", builderMethodName = "existing")
    public ShoppingCart(ShoppingCartId id, CustomerId customerId,
                        Money totalAmount, Quantity totalItems, OffsetDateTime createdAt,
                        Set<ShoppingCartItem> items) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setCreatedAt(createdAt);
        this.setItems(items);
    }

    public static ShoppingCart startShopping(CustomerId customerId){
        return new ShoppingCart(new ShoppingCartId(), customerId, Money.ZERO,
                Quantity.ZERO, OffsetDateTime.now(), new HashSet<>());
    }

    public void empty(){
        this.items.clear();
        totalAmount = Money.ZERO;
        totalItems = Quantity.ZERO;
    }

    public void addItem(Product product, Quantity quantity){
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        product.checkOutOfStock();

        ShoppingCartItem shoppingCartItem = ShoppingCartItem.brandNew()
                .shoppingCartId(this.id)
                .productId(product.id())
                .productName(product.name())
                .price(product.price())
                .available(product.inStock())
                .quantity(quantity)
                .build();

        Optional<ShoppingCartItem> shoppingCartItemOptional = this.items.stream()
                .filter(i -> i.productId().equals(product.id()))
                .findFirst();

        shoppingCartItemOptional
                .ifPresentOrElse(i -> updateItem(i, product, quantity),
                        () -> this.items.add(shoppingCartItem));

        this.recalculateTotals();
    }

    public void removeItem(ShoppingCartItemId shoppingCartItemId){
        Objects.requireNonNull(shoppingCartItemId);

        ShoppingCartItem shoppingCartItem = this.findItem(shoppingCartItemId);
        this.items.remove(shoppingCartItem);
        this.recalculateTotals();
    }

    public void refreshItem(Product product){
        Objects.requireNonNull(product);
        ShoppingCartItem shoppingCartItem = this.findItem(product.id());
        shoppingCartItem.refresh(product);
        this.recalculateTotals();
    }

    public void changeItemQuantity(ShoppingCartItemId shoppingCartItemId, Quantity quantity){
        Objects.requireNonNull(shoppingCartItemId);
        Objects.requireNonNull(quantity);

        ShoppingCartItem shoppingCartItem = this.findItem(shoppingCartItemId);
        shoppingCartItem.changeQuantity(quantity);
        this.recalculateTotals();
    }


    public ShoppingCartItem findItem(ShoppingCartItemId shoppingCartItemId){
        Objects.requireNonNull(shoppingCartItemId);

        return this.items.stream()
                .filter(i -> i.id().equals(shoppingCartItemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainItemException(this.id, shoppingCartItemId));
    }

    public ShoppingCartItem findItem(ProductId productId){
        Objects.requireNonNull(productId);

        return this.items.stream()
                .filter(i -> i.productId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainProductException(this.id, productId));
    }

    public void recalculateTotals(){

        BigDecimal totalAmount = this.items.stream()
                .map(i -> i.totalAmount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = this.items().stream().map(i -> i.quantity().value())
                .reduce(0, Integer::sum);

        this.setTotalAmount(new Money(totalAmount));
        this.setTotalItems(new Quantity(totalItems));
    }

    public boolean containsUnavailableItems(){
        return items.stream().anyMatch(i -> !i.isAvailable());
    }

    public boolean isEmpty(){
        return this.items.isEmpty();
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(items);
    }

    public ShoppingCartId id() { return this.id; }

    public CustomerId customerId() { return this.customerId; }

    public Money totalAmount() { return this.totalAmount; }

    public Quantity totalItems() { return this.totalItems; }

    public OffsetDateTime createdAt() { return this.createdAt; }

    public Long version(){
        return version;
    }

    private void setVersion(Long version){
        this.version = version;
    }

    private void updateItem(ShoppingCartItem shoppingCartItem, Product product, Quantity quantity) {
        shoppingCartItem.refresh(product);
        shoppingCartItem.changeQuantity(shoppingCartItem.quantity().add(quantity));
    }

    private void setId(ShoppingCartId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId){
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setCreatedAt(OffsetDateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
    }

    private void setItems(Set<ShoppingCartItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    @Override
    public String toString() {
        return  id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
