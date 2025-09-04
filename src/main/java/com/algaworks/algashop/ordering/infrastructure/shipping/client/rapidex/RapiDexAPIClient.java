package com.algaworks.algashop.ordering.infrastructure.shipping.client.rapidex;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface RapiDexAPIClient {

    @PostExchange("/api/delivery-cost")
    public DeliveryCostResponse calculate(@RequestBody DeliveryCostRequest deliveryCostRequest);
}
