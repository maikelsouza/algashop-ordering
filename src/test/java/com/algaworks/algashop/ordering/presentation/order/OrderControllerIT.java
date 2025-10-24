package com.algaworks.algashop.ordering.presentation.order;

import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInputTestDataBuilder;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntityTestDataBuilder.existingShoppingCart;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL,
//        ids = "com.algaworks.algashop:product-catalog:0.0.1-SNAPSHOT:8781")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerIT {

    @LocalServerPort
    private int port;

    private static boolean databaseInitialized;

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    @Autowired
    private OrderPersistenceEntityRepository orderRepository;

    @Autowired
    private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    private static final UUID validProductId = UUID.fromString("fffe6ec2-7103-48b3-8e4f-3b58e43fb75a");

    private static final UUID validShoppingCartId = UUID.fromString("4f31582a-66e6-4601-a9d3-ff608c2d4461");

    private WireMockServer wireMockProductCatalog;

    private WireMockServer wireMockRapidex;


    @BeforeEach
    public void setup(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);

        initDatabase();

        wireMockProductCatalog = new WireMockServer(
                options()
                    .port(8781)
                    .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog")
                    .extensions(new ResponseTemplateTransformer(true)));

        wireMockRapidex = new WireMockServer(
                options()
                    .port(8780)
                    .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex")
                    .extensions(new ResponseTemplateTransformer(true)));

        wireMockProductCatalog.start();
        wireMockRapidex.start();
    }

    @AfterEach
    public void after(){
        wireMockProductCatalog.stop();
        wireMockRapidex.stop();
    }

    private void initDatabase(){
        customerRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(validCustomerId).build());
    }

    @Test
    public void shouldCreateOrderUsingProduct(){
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");
        String createdOrderId = RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType("application/vnd.order-with-product.v1+json")
                    .body(json)
                .when()
                    .post("/api/v1/orders")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", Matchers.not(Matchers.emptyString()),
                            "customer.id", Matchers.is(validCustomerId.toString()))
                    .extract().jsonPath().getString("id");

        boolean orderExists = orderRepository.existsById(new OrderId(createdOrderId).value().toLong());

        Assertions.assertThat(orderExists).isTrue();
    }

    @Test
    public void shouldCreateOrderUsingProduct_DTO(){
        BuyNowInput input = BuyNowInputTestDataBuilder.aBuyNowInput()
                .productId(validProductId)
                .customerId(validCustomerId)
                .build();

        OrderDetailOutput orderDetailOutput = RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType("application/vnd.order-with-product.v1+json")
                    .body(input)
                .when()
                    .post("/api/v1/orders")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", Matchers.not(Matchers.emptyString()),
                            "customer.id", Matchers.is(validCustomerId.toString()))
                    .extract().body().as(OrderDetailOutput.class);


        Assertions.assertThat(orderDetailOutput.getCustomer().getId()).isEqualTo(validCustomerId);

        boolean orderExists = orderRepository.existsById(new OrderId(orderDetailOutput.getId()).value().toLong());

        Assertions.assertThat(orderExists).isTrue();
    }

    @Test
    public void shouldNotCreateOrderUsingProductWhenProductAPIIsUnavailable(){
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product.json");

        wireMockProductCatalog.stop();

        RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType("application/vnd.order-with-product.v1+json")
                    .body(json)
                .when()
                    .post("/api/v1/orders")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .statusCode(HttpStatus.GATEWAY_TIMEOUT.value());
    }

    @Test
    public void shouldNotCreateOrderUsingProductWhenProductNotExists(){
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-invalid-product.json");

        RestAssured
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType("application/vnd.order-with-product.v1+json")
                    .body(json)
                .when()
                    .post("/api/v1/orders")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void shouldNotCreateOrderUsingProductWhenCustomerWasNotFound() {
        String json = AlgaShopResourceUtils.readContent("json/create-order-with-product-and-invalid-customer.json");
        RestAssured
             .given()
                 .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void shouldCreateOrderUsingShoppingCart(){

        var shoppingCartPersistence = existingShoppingCart()
                .id(validShoppingCartId)
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();
        shoppingCartRepository.save(shoppingCartPersistence);

        String json = AlgaShopResourceUtils.readContent("json/create-order-with-shopping-cart.json");
        String createdOrderId = RestAssured
                .given()
                   .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType("application/vnd.order-with-shopping-cart.v1+json")
                    .body(json)
                .when()
                    .post("/api/v1/orders")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", Matchers.not(Matchers.emptyString()),
                    "customer.id", Matchers.is(validCustomerId.toString()))
                .extract().body().as(OrderDetailOutput.class).getId();

        boolean orderExists = orderRepository.existsById(new OrderId(createdOrderId).value().toLong());

        Assertions.assertThat(orderExists).isTrue();
    }

    @Test
    public void shouldNotCreateOrderUsingShoppingCartWhenShoppingCartWasNotFound(){

        var shoppingCartPersistence = existingShoppingCart()
                .id(validShoppingCartId)
                .customer(customerRepository.getReferenceById(validCustomerId))
                .build();
        shoppingCartRepository.save(shoppingCartPersistence);

        String json = AlgaShopResourceUtils.readContent("json/create-order-with-shopping-cart-was-not-found.json");
        RestAssured
            .given()
               .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-shopping-cart.v1+json")
                .body(json)
            .when()
                .post("/api/v1/orders")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }


}
