package com.algaworks.algashop.ordering.presentation.order;

import com.algaworks.algashop.ordering.application.checkout.BuyNowApplicationService;
import com.algaworks.algashop.ordering.application.checkout.BuyNowInput;
import com.algaworks.algashop.ordering.application.checkout.CheckoutApplicationService;
import com.algaworks.algashop.ordering.application.checkout.CheckoutInput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderFilter;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.presentation.PageModel;
import com.algaworks.algashop.ordering.presentation.UnprocessableEntityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderQueryService orderQueryService;

    private final BuyNowApplicationService buyNowApplicationService;

    private final CheckoutApplicationService checkoutApplicationService;

    @GetMapping("/{orderId}")
    public OrderDetailOutput findById(@PathVariable String orderId){
        return orderQueryService.findById(orderId);
    }

    @GetMapping
    public PageModel<OrderSummaryOutput> filter(OrderFilter filter) {
        return PageModel.of(orderQueryService.filter(filter));
    }

    @PostMapping(consumes = "application/vnd.order-with-product.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailOutput createWithProduct(@Valid @RequestBody BuyNowInput input) {
        String orderId;
        try {
            orderId = buyNowApplicationService.buyNow(input);
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return orderQueryService.findById(orderId);
    }

    @PostMapping(consumes = "application/vnd.order-with-shopping-cart.v1+json")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailOutput created(@RequestBody @Valid CheckoutInput input){
        String orderId;
        try{
            orderId = checkoutApplicationService.checkout(input);
        } catch (CustomerNotFoundException | ShoppingCartNotFoundException e){
           throw new UnprocessableEntityException(e.getMessage(), e);
        }
        return orderQueryService.findById(orderId);

    }



}
