package com.algaworks.algashop.ordering.presentation;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerContractTest {

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setupAll(){
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8).build());
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void createCustomerContract(){
        String jsonInput = """
                {
                  "id": "380d7e70-7942-4b8d-a0e9-b93d79aa6745",
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "Johndoe@email.com",
                  "document": "12345",
                  "phone": "1191234564",
                  "birthDate": "1991-07-05",
                  "registerAt": "2024-04-04T12:45Z",
                  "archevidAt": "2024-04-04T12:45Z",                  
                  "promotionNotificationsAllowed": false,
                  "address": {
                    "street": "Bourbon Street",
                    "number": "2000",
                    "complement": "apt 122",
                    "neighborhood": "North Ville",
                    "city": "Yostford",
                    "state": "South Line",
                    "zipCode": "12321"
                  }
                }
                """;
        RestAssuredMockMvc
                .given()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .body(jsonInput)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/api/v1/customers")
                .then()
                    .assertThat()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .statusCode(HttpStatus.CREATED.value())
                    .body(
                         "id", Matchers.notNullValue(),
                    "registeredAt", Matchers.notNullValue(),
                            "firstName", Matchers.is("John"),
                            "lastName", Matchers.is("Doe"),
                            "document", Matchers.is("12345"),
                            "phone", Matchers.is("1191234564"),
                            "birthDate", Matchers.is("1991-07-05"),
                            "promotionNotificationsAllowed", Matchers.is(false),
                            "address.street", Matchers.is("Bourbon Street"),
                            "address.number", Matchers.is("2000"),
                            "address.complement", Matchers.is("apt 122"),
                            "address.neighborhood", Matchers.is("North Ville"),
                            "address.city", Matchers.is("Yostford"),
                            "address.state", Matchers.is("South Line"),
                            "address.zipCode", Matchers.is("12321")
                    );

    }
}