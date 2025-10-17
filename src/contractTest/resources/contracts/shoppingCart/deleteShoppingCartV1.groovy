package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    request {
        method DELETE()
        headers {
            accept 'application/json'
        }
        urlPath("/api/v1/shopping-carts/f1a3b8e4-7c52-4a90-9d83-1e7c65a2b9d4")
    }

    response {
        status 204
    }
}