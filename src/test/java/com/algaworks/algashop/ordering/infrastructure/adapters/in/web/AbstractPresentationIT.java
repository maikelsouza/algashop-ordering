package com.algaworks.algashop.ordering.infrastructure.adapters.in.web;

import com.algaworks.algashop.ordering.utils.TestContainerPostgreSQLConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:db/testdata/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:db/clean/afterMigrate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Import(TestContainerPostgreSQLConfig.class)
public abstract class AbstractPresentationIT {

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
                        .templatingEnabled(true)
                        .port(8781)
                        .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog"));


        wireMockRapidex = new WireMockServer(
                options()
                        .templatingEnabled(true)
                        .port(8780)
                        .usingFilesUnderDirectory("src/test/resources/wiremock/rapidex"));


        wireMockProductCatalog.start();
        wireMockRapidex.start();
    }

    protected static void stopMock() {
        wireMockProductCatalog.stop();
        wireMockRapidex.stop();
    }


}
