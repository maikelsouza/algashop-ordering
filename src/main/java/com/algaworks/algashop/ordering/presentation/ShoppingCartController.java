package com.algaworks.algashop.ordering.presentation;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartItemInput;
import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartManagementApplicationService;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartInput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-carts")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final  ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;

    private final ShoppingCartQueryService shoppingCartQueryService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartOutput create(@RequestBody @Valid ShoppingCartInput input) {
        UUID shoppingCartId;
        try{
            shoppingCartId = shoppingCartManagementApplicationService.createNew(input.getCustomerId());
        }catch (CustomerNotFoundException e){
            throw new UnprocessableEntityException(e.getMessage(), e);
        }

        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}")
    public ShoppingCartOutput findById(@PathVariable UUID shoppingCartId){
        return shoppingCartQueryService.findById(shoppingCartId);
    }

    @GetMapping("/{shoppingCartId}/items")
    public ShoppingCartItemListModel findItems(@PathVariable UUID shoppingCartId){
        List<ShoppingCartItemOutput> items = shoppingCartQueryService.findById(shoppingCartId).getItems();
        return new ShoppingCartItemListModel(items);
    }

    @DeleteMapping("/{shoppingCartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID shoppingCartId){
        shoppingCartManagementApplicationService.delete(shoppingCartId);
    }

    @DeleteMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void empty(@PathVariable UUID shoppingCartId){
        shoppingCartManagementApplicationService.empty(shoppingCartId);
    }

    @PostMapping("/{shoppingCartId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addItem(@PathVariable UUID shoppingCartId, @RequestBody @Valid ShoppingCartItemInput input){
        input.setShoppingCartId(shoppingCartId);
        try{
            shoppingCartManagementApplicationService.addItem(input);
        } catch (ProductNotFoundException e){
            throw new UnprocessableEntityException(e.getMessage(), e);
        }

    }

    @DeleteMapping("/{shoppingCartId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable UUID shoppingCartId, @PathVariable UUID itemId){
        shoppingCartManagementApplicationService.removeItem(shoppingCartId, itemId);
    }

}
