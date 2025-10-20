package contracts.shoppingCart

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method POST()
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
        urlPath("/api/v1/shopping-carts")
        body([
            customerId: value(test(anyUuid()), stub(anyUuid()))
        ])
    }
    response {
        status 201
        headers {
            contentType 'application/json'
        }
        body([
                id: anyUuid(),
                customerId: anyUuid(),
                totalItems: 3,
                totalAmount: 1250,
                items: [
                    [
                        id: anyNonBlankString(),
                        productId: anyUuid(),
                        name: anyNonBlankString(),
                        price: 500,
                        quantity: 2,
                        totalAmount: 1000,
                        available: anyBoolean()
                    ],
                    [
                        id: anyNonBlankString(),
                        productId: anyUuid(),
                        name: anyNonBlankString(),
                        price: 250,
                        quantity: 1,
                        totalAmount: 250,
                        available: anyBoolean()
                    ]
                ]
        ])
    }
}