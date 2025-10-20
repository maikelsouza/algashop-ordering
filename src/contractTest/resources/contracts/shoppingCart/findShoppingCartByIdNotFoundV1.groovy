package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept "application/json"
        }
        urlPath("/api/v1/shopping-carts/9c7e21d4-3a5f-4d8b-8a19-2f4e7bde3f51")
    }
    response {
        status 404
        body([
                instance: fromRequest().path(),
                type: "/errors/not-found",
                title: "Not Found"
        ])
    }
}