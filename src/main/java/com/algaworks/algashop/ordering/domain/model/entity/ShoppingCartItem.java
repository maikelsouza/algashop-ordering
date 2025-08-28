package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartItemIncompatibleProductException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import lombok.Builder;

import java.util.Objects;

public class ShoppingCartItem {

    private ShoppingCartItemId id;

    private ShoppingCartId shoppingCartId;

    private ProductId productId;

    private ProductName productName;

    private Money price;

    private Quantity quantity;

    private Money totalAmount;

    private Boolean available;


    @Builder(builderClassName = "ExistingShoppingCartItemBuilder", builderMethodName = "existing")
    public ShoppingCartItem(ShoppingCartItemId id, ShoppingCartId shoppingCartId, ProductId productId, ProductName productName,
                            Money price, Quantity quantity,Boolean available, Money totalAmount) {
        this.setId(id);
        this.setShoppingCartId(shoppingCartId);
        this.setProductId(productId);
        this.setProductName(productName);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setTotalAmount(totalAmount);
        this.setAvailable(available);
    }


    @Builder(builderClassName = "BrandNewShoppingCartItem", builderMethodName = "brandNew")
    public ShoppingCartItem(ShoppingCartId shoppingCartId,
                            ProductId productId, ProductName productName, Money price,
                            Quantity quantity, Boolean available) {
        this(new ShoppingCartItemId(), shoppingCartId, productId, productName, price, quantity, available, Money.ZERO);
        this.recalculateTotals();
    }


    void refresh(Product product){
        if (!this.productId.equals(product.id())){
            throw new ShoppingCartItemIncompatibleProductException(this.productId, product.id());
        }

        this.setPrice(product.price());
        this.setProductName(product.name());
        this.setAvailable(product.inStock());

        this.recalculateTotals();
    }

    void changeQuantity(Quantity quantity){
        Objects.requireNonNull(quantity);
        setQuantity(quantity);
        this.recalculateTotals();
    }

    public void recalculateTotals(){
        this.setTotalAmount(this.price.multiply(this.quantity));
    }

    public ShoppingCartItemId id() { return id; }

    public ShoppingCartId shoppingCardId() { return this.shoppingCartId; }

    public ProductId productId() { return this.productId; }

    public ProductName name() { return this.productName; }

    public Money price() { return this.price; }

    public Money totalAmount() { return  totalAmount; }

    public Quantity quantity() { return quantity; }

    public Boolean isAvailable() { return available; }

    private void setId(ShoppingCartItemId id) {
        this.id = id;
    }

    private void setShoppingCartId(ShoppingCartId shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    private void setProductId(ProductId productId) {
        this.productId = productId;
    }

    private void setProductName(ProductName productName) {
        this.productName = productName;
    }

    private void setPrice(Money price) {
        this.price = price;
    }

    private void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    private void setAvailable(Boolean available) {
        this.available = available;
    }

    private void setQuantity(Quantity quantity){
        if (quantity.value() <= 0){
            throw new IllegalArgumentException();
        }
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartItem that = (ShoppingCartItem) o;
        return Objects.equals(id, that.id);
    }



    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
