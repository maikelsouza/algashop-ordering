package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
        urlPath("/api/v1/shopping-carts/f1a3b8e4-7c52-4a90-9d83-1e7c65a2b9d4/items")
        body([
                productId: value(
                        test("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                        stub(anyUuid())
                ),
                quantity: value(
                        test(2),
                        stub(anyPositiveInt())
                )
        ])
    }
    response {
        status 204
    }
}