package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {
        method DELETE()
        urlPath("/api/v1/shopping-carts/f1a3b8e4-7c52-4a90-9d83-1e7c65a2b9d4/items/e2b14f7a-6d3c-4f91-9c2e-8a5f7b1d4c23")
    }

    response {
        status 204
    }
}