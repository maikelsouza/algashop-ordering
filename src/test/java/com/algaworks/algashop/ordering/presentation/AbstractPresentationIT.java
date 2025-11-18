package com.algaworks.algashop.ordering.presentation;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/testdata/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Testcontainers
public abstract class AbstractPresentationIT {


    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("ordering_test");

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockProductCatalog;

    protected static WireMockServer wireMockRapidex;

    protected void beforeEach(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        JsonConfig jsonConfig = JsonConfig.jsonConfig()
                .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL);
        RestAssured.config().jsonConfig(jsonConfig);
    }

    protected static void initWireMock() {
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

    protected static void stopMock() {
        wireMockProductCatalog.stop();
        wireMockRapidex.stop();
    }


}
