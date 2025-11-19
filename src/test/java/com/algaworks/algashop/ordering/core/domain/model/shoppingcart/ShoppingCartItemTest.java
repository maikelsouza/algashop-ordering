package com.algaworks.algashop.ordering.core.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.core.domain.model.commons.Money;
import com.algaworks.algashop.ordering.core.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.core.domain.model.product.Product;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductName;
import com.algaworks.algashop.ordering.core.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.algaworks.algashop.ordering.core.domain.model.ErrorMessages.ERROR_SHOPPING_CARD_ITEM_INCOMPATIBLE_PRODUCT;

public class ShoppingCartItemTest {


    @Test
    public void givenShoppingCartItem_shouldGeneration(){
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertThat(shoppingCartItem.totalAmount()).isEqualTo(shoppingCartItem.price().multiply(shoppingCartItem.quantity()));
    }

    @Test
    public void givenShoppingCartItem_whenTryModifyQuantityForZero_shouldGenerationException(){
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> shoppingCartItem.changeQuantity(Quantity.ZERO));
    }

    @Test
    public void givenShoppingCartItem_whenIncompatibleProduct_shouldGenerationException(){
        ShoppingCartItem shoppingCartItem = ShoppingCartItemTestDataBuilder.aShoppingCartItem().build();
        Product product = ProductTestDataBuilder.aProduct().build();

        Assertions.assertThatExceptionOfType(ShoppingCartItemIncompatibleProductException.class)
                .isThrownBy(() -> shoppingCartItem.refresh(product))
                .withMessage(String.format(ERROR_SHOPPING_CARD_ITEM_INCOMPATIBLE_PRODUCT, shoppingCartItem.productId(), product.id()));
    }

    @Test
    public void givenTwoShoppingCartItems_wheSameIds_shouldBeEquals() {

        ShoppingCartItemId shoppingCartItemId = new ShoppingCartItemId();

        ShoppingCartItem shoppingCartItem = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(new ShoppingCartId())
                .productId(new ProductId())
                .productName(new ProductName("Notebook"))
                .price(new Money("5000"))
                .quantity(new Quantity(1))
                .available(true)
                .totalAmount(new Money("10"))
                .build();


        ShoppingCartItem shoppingCartItem2 = ShoppingCartItem.existing()
                .id(shoppingCartItemId)
                .shoppingCartId(new ShoppingCartId())
                .productId(new ProductId())
                .productName(new ProductName("Notebook"))
                .price(new Money("5000"))
                .quantity(new Quantity(1))
                .available(true)
                .totalAmount(new Money("10"))
                .build();


        Assertions.assertThat(shoppingCartItem).isEqualTo(shoppingCartItem2);
    }

}
