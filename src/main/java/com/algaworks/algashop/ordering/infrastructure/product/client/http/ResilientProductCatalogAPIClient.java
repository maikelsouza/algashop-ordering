package com.algaworks.algashop.ordering.infrastructure.product.client.http;

import com.algaworks.algashop.ordering.presentation.BadGatewayException;
import com.algaworks.algashop.ordering.presentation.GatewayTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResilientProductCatalogAPIClient {

    private final ProductCatalogAPIClient productCatalogAPIClient;

    @Cacheable(cacheNames = "algashop:products-catalog-api:v1", key = "#productId")
    @ConcurrencyLimit(2)
    @Retryable(
            maxRetries = 3,
            delayString ="3s",
            multiplier = 2,
            includes = {GatewayTimeoutException.class, BadGatewayException.class})
    public Optional<ProductResponse> getId(UUID productId) {
        log.info("Loading product {}", productId);
        try {
            return Optional.ofNullable(productCatalogAPIClient.getById(productId));
        } catch (ResourceAccessException e){
            throw new GatewayTimeoutException("Product Catalog API Timeout", e);
        } catch (HttpClientErrorException.NotFound e){
            return Optional.empty();
        } catch (RestClientException e){
            if (e.getCause() instanceof java.net.SocketTimeoutException){
                throw new GatewayTimeoutException("Product Catalog API Timeout", e);
            }
            throw new BadGatewayException("Product Catalog API Bad Gateway", e);
        }
    }

}
