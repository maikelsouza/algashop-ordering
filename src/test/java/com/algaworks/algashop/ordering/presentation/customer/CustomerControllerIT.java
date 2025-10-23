package com.algaworks.algashop.ordering.presentation.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerPersistenceEntityRepository customerRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a");

    private static final UUID invalidCustomerId = UUID.fromString("a7f4c8b9-2d35-4871-9e3c-4b62f1a9d7e5");

    @BeforeEach
    public void setup(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);

        initDatabase();


    }

    private void initDatabase(){
        customerRepository.saveAndFlush(CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(validCustomerId).build());
    }

    @Test
    public void shouldCreateACustomer(){
        String json = AlgaShopResourceUtils.readContent("json/create-customer.json");
        UUID createdCustomerId = RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString()))
                .extract()
                .jsonPath().getUUID("id");

        boolean customerExists = customerRepository.existsById(createdCustomerId);

        Assertions.assertThat(customerExists).isTrue();
    }

    @Test
    public void notShouldCreateACustomerWithRequiredFields(){
        String json = AlgaShopResourceUtils.readContent("json/create-customer-required-fields.json");
        RestAssured
            .given()
               .accept(MediaType.APPLICATION_JSON_VALUE)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
               .body(json)
            .when()
                .post("/api/v1/customers")
            .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldDeleteACustomer(){
        RestAssured
            .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .delete("/api/v1/customers/"+validCustomerId)
            .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Assertions.assertThat(customerRepository.existsById(validCustomerId)).isTrue();
        Assertions.assertThat(customerRepository.findById(validCustomerId).orElseThrow().getArchived()).isTrue();
    }

    @Test
    public void shouldTryDeleteACustomerNotExisting(){
        RestAssured
            .given()
                 .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
                 .delete("/api/v1/customers/"+invalidCustomerId)
            .then()
                 .assertThat()
                 .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
