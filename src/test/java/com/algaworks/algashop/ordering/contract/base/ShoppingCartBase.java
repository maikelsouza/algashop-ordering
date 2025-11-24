package com.algaworks.algashop.ordering.contract.base;

import com.algaworks.algashop.ordering.core.application.shoppingcart.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.core.application.shoppingcart.ShoppingCartOutputTestDataBuilder;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.core.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.core.ports.in.shoppingcart.ForQueringShoppingCarts;
import com.algaworks.algashop.ordering.infrastructure.adapters.in.web.shoppingcart.ShoppingCartController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ShoppingCartController.class)
@ExtendWith(SpringExtension.class)
public class ShoppingCartBase {


    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    @MockitoBean
    private ForQueringShoppingCarts shoppingCartQueryService;

    public static final UUID invalidShoppingCarId = UUID.fromString("9c7e21d4-3a5f-4d8b-8a19-2f4e7bde3f51");

    public static final UUID validShoppingCarId = UUID.fromString("f1a3b8e4-7c52-4a90-9d83-1e7c65a2b9d4");

    @BeforeEach
    void setUp(){

        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context)
                        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                        .build()
        );
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();

        mockValidShoppingCartId();
        mockInvalidShoppingCartId();
        mockCreateShoppingCart();
    }

    private void mockValidShoppingCartId() {
        when(shoppingCartQueryService.findById(validShoppingCarId))
                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCartOutput().id(validShoppingCarId).build());
    }

    private void mockInvalidShoppingCartId(){
        Mockito.when(shoppingCartQueryService.findById(invalidShoppingCarId))
                .thenThrow(new ShoppingCartNotFoundException(new ShoppingCartId(invalidShoppingCarId)));
    }

    private void mockCreateShoppingCart(){
        when(shoppingCartManagementApplicationService.createNew(Mockito.any(UUID.class)))
                .thenReturn(validShoppingCarId);

        when(shoppingCartQueryService.findById(validShoppingCarId))
                .thenReturn(ShoppingCartOutputTestDataBuilder.aShoppingCartOutput().id(validShoppingCarId).build());
    }

}
