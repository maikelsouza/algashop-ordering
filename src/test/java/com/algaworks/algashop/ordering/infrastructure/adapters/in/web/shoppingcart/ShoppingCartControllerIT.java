package com.algaworks.algashop.ordering.infrastructure.adapters.in.web.shoppingcart;

import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.AbstractPresentationIT;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;


public class ShoppingCartControllerIT extends AbstractPresentationIT {

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    private static final UUID validShoppingCartId = UUID.fromString("4f31582a-66e6-4601-a9d3-ff608c2d4461");

    private static final UUID invalidShoppingCartId = UUID.fromString("a7f4c8b9-2d35-4871-9e3c-4b62f1a9d7e5");



    @BeforeEach
    public void setup(){
        super.beforeEach();
    }

    @BeforeAll
    public static void setupBeforeAll(){
        AbstractPresentationIT.initWireMock();
    }


    @AfterAll
    public static void afterAll(){
        AbstractPresentationIT.stopMock();
    }

    @Test
    public void shouldCreateAShoppingCart(){
        String json = AlgaShopResourceUtils.readContent("json/create-shopping-cart.json");
        UUID createdShoppingCartId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()))
                .extract()
                .jsonPath().getUUID("id");

        boolean shoppingCartExists = shoppingCartRepository.existsById(createdShoppingCartId);

        Assertions.assertThat(shoppingCartExists).isTrue();
    }

    @Test
    public void shouldNotCreateAShoppingCartWithRequiredField(){
        String json = AlgaShopResourceUtils.readContent("json/create-shopping-cart-required-field.json");
        RestAssured
            .given()
              .accept(MediaType.APPLICATION_JSON_VALUE)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(json)
            .when()
                .post("/api/v1/shopping-carts")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldAddItemToShoppingCart(){
        String json = AlgaShopResourceUtils.readContent("json/add-product-to-shopping-cart.json");
        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items",validShoppingCartId)
            .then()
                .assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value());

        ShoppingCartPersistenceEntity shoppingCartPersistenceEntity = shoppingCartRepository.findById(validShoppingCartId).orElseThrow();
        Assertions.assertThat(shoppingCartPersistenceEntity.getTotalItems()).isEqualTo(4);
    }

    @Test
    public void shouldReturnNotFoundWhenAddingItemToNonexistentShoppingCart(){

        String json = AlgaShopResourceUtils.readContent("json/add-product-to-shopping-cart.json");
        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/shopping-carts/{shoppingCartId}/items", invalidShoppingCartId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
