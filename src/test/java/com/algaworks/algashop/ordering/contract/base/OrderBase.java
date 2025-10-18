package com.algaworks.algashop.ordering.contract.base;

import com.algaworks.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.algaworks.algashop.ordering.application.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.application.order.query.*;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotFoundException;
import com.algaworks.algashop.ordering.presentation.order.OrderController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

@WebMvcTest(controllers = OrderController.class)
@ExtendWith(SpringExtension.class)
public class OrderBase {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private CheckoutApplicationService checkoutApplicationService;

    @MockitoBean
    private BuyNowApplicationService buyNowApplicationService;

    public static final String validOrderId = "01226N0640J7Q";

    public static final String notFoundOrderId = "01226N0693HDH";

    @BeforeEach
    void setUp(){
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context)
                        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                        .build()
        );
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();


        mockValidOrderFindById();
        mockNotFoundOrderFindById();
        mockFilterOrders();
        mockValidBuyNow();
        mockValidCheckout();
    }

    private void mockValidCheckout() {
        Mockito.when(checkoutApplicationService.checkout(Mockito.any(CheckoutInput.class)))
                .thenReturn(validOrderId);
    }

    private void mockValidBuyNow() {
        Mockito.when(buyNowApplicationService.buyNow(Mockito.any(BuyNowInput .class)))
                .thenReturn(validOrderId);
    }

    private void mockValidOrderFindById() {
        Mockito.when(orderQueryService.findById(validOrderId))
                .thenReturn(OrderDetailOutputTestDataBuilder.placedOrder(validOrderId).build());
    }

    private void mockNotFoundOrderFindById() {
        Mockito.when(orderQueryService.findById(notFoundOrderId))
                .thenThrow(new OrderNotFoundException(new OrderId(notFoundOrderId)));
    }

    private void mockFilterOrders() {
        Mockito.when(orderQueryService.filter(Mockito.any(OrderFilter.class)))
            .thenReturn(new PageImpl<>(
                    List.of(OrderSummaryOutputTestDataBuilder.placedOrder().id(validOrderId).build())
            ));
    }




}
